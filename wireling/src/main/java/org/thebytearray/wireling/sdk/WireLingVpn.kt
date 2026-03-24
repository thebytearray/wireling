/*
 WireLing
 https://github.com/thebytearray/wireling

 Created by Tamim Hossain.
 Copyright (c) 2025 The Byte Array LTD.

 This file is part of the WireLing library.

 WireLing is free software: you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by the Free
 Software Foundation, either version 3 of the License, or (at your option) any later version.

 WireLing is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 details.

 You should have received a copy of the GNU General Public License
 along with WireLing. If not, see <https://www.gnu.org/licenses/>.
*/

package org.thebytearray.wireling.sdk

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import org.thebytearray.wireling.sdk.domain.TunnelConfig
import org.thebytearray.wireling.sdk.platform.TunnelIntents
import org.thebytearray.wireling.sdk.platform.TunnelService

/**
 * Application-facing entry point for WireLing: notification setup, runtime permissions,
 * and starting/stopping the VPN [TunnelService].
 *
 * Call [createNotificationChannel] and [setNotificationIcon] before [startVpnTunnel].
 */
public object WireLingVpn {
    private const val TAG = "WireLingVpn"

    @Volatile
    private var iconResId: Int = 0

    @Volatile
    private var channelIdInternal: String = ""

    /** Drawable resource id last passed to [setNotificationIcon], or `0` if unset. */
    public val notificationIconResId: Int
        get() = iconResId

    /** Notification channel id last passed to [createNotificationChannel], or empty if unset. */
    public val notificationChannelId: String
        get() = channelIdInternal

    /**
     * Sets the small icon used for the VPN foreground notification.
     * Required before [startVpnTunnel].
     */
    @JvmStatic
    public fun setNotificationIcon(resId: Int) {
        iconResId = resId
    }

    /**
     * Creates the notification channel on API 26+ and remembers [channelId] for the tunnel service.
     * Call from [android.app.Application.onCreate] or before [startVpnTunnel].
     *
     * @param channelId Stable id for this channel (user-visible in system settings).
     * @param channelName User-visible channel name.
     * @param importance One of [NotificationManager] importance constants.
     */
    @JvmStatic
    @JvmOverloads
    public fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_HIGH,
    ) {
        channelIdInternal = channelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $channelId")
        }
    }

    /** `true` if VPN permission is already granted ([VpnService.prepare] returns `null`). */
    @JvmStatic
    public fun hasVpnPermission(context: Context): Boolean {
        return VpnService.prepare(context) == null
    }

    /**
     * If non-null, an intent the user must confirm to grant VPN access; launch with
     * [registerVpnPermissionLauncher] and [launchVpnPermissionFlow]. If `null`, permission is already granted.
     */
    @JvmStatic
    public fun vpnPermissionPreparationIntent(context: Context): Intent? {
        return VpnService.prepare(context)
    }

    /**
     * Registers an [ActivityResultLauncher] for the system VPN consent activity.
     * Invoke from [androidx.activity.ComponentActivity.onCreate] before STARTED.
     */
    @JvmStatic
    public fun registerVpnPermissionLauncher(
        activity: ComponentActivity,
        onResult: (Boolean) -> Unit,
    ): ActivityResultLauncher<Intent> {
        return activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            onResult(result.resultCode == Activity.RESULT_OK)
        }
    }

    /**
     * Launches [preparationIntent] when VPN permission is needed, or runs [onAlreadyGranted] if
     * [vpnPermissionPreparationIntent] was `null`.
     */
    @JvmStatic
    public fun launchVpnPermissionFlow(
        launcher: ActivityResultLauncher<Intent>,
        preparationIntent: Intent?,
        onAlreadyGranted: () -> Unit,
    ) {
        if (preparationIntent != null) {
            launcher.launch(preparationIntent)
        } else {
            onAlreadyGranted()
        }
    }

    /**
     * On API 33+, `true` only if [Manifest.permission.POST_NOTIFICATIONS] is granted.
     * On older APIs, always `true`.
     */
    @JvmStatic
    public fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Registers a launcher for [Manifest.permission.POST_NOTIFICATIONS] (API 33+).
     * Callback receives `true` if granted.
     */
    @JvmStatic
    public fun registerPostNotificationsLauncher(
        activity: ComponentActivity,
        onResult: (Boolean) -> Unit,
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            onResult(granted)
        }
    }

    /** Requests POST_NOTIFICATIONS on API 33+; no-op on older versions. */
    @JvmStatic
    public fun launchPostNotificationsRequest(launcher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * Starts [TunnelService] as a foreground VPN using [config].
     *
     * @param blockedApps Package names excluded from the tunnel, or `null`/empty for none.
     * @throws IllegalArgumentException if notification icon or channel was not configured.
     */
    @JvmStatic
    public fun startVpnTunnel(context: Context, config: TunnelConfig, blockedApps: List<String>?) {
        require(iconResId != 0) {
            "Notification icon must be set before starting VPN. Call setNotificationIcon() first."
        }
        require(channelIdInternal.isNotEmpty()) {
            "Notification channel must be created before starting VPN. Call createNotificationChannel() first."
        }

        try {
            val startIntent = Intent(context, TunnelService::class.java).apply {
                putStringArrayListExtra(
                    TunnelIntents.BLOCKED_APPS,
                    ArrayList(blockedApps ?: emptyList()),
                )
                putExtra(TunnelIntents.TUNNEL_CONFIG, config)
                putExtra(TunnelIntents.NOTIFICATION_ICON, iconResId)
            }
            startTunnelService(context, startIntent)
            Log.d(TAG, "VPN tunnel service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN tunnel service", e)
            throw e
        }
    }

    /** Asks [TunnelService] to tear down the VPN tunnel. */
    @JvmStatic
    public fun stopVpnTunnel(context: Context) {
        try {
            val stopIntent = Intent(context, TunnelService::class.java).apply {
                action = TunnelIntents.STOP_ACTION
            }
            startTunnelService(context, stopIntent)
            Log.d(TAG, "VPN tunnel service stopped successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop VPN tunnel service", e)
            throw e
        }
    }

    private fun startTunnelService(context: Context, intent: Intent) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                context.startForegroundService(intent)
            } else {
                @Suppress("DEPRECATION")
                context.startService(intent)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to start service due to security exception", e)
            throw e
        }
    }
}
