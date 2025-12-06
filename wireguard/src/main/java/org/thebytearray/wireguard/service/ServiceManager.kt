package org.thebytearray.wireguard.service

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.thebytearray.wireguard.model.TunnelConfig
import org.thebytearray.wireguard.util.Constants.BLOCKED_APPS
import org.thebytearray.wireguard.util.Constants.STOP_ACTION
import org.thebytearray.wireguard.util.Constants.TUNNEL_CONFIG

/**
 * TheByteArray
 *
 * @developer Tamim Hossain
 * @mail contact@thebytearray.org
 */
object ServiceManager {
    private const val TAG = "ServiceManager"
    private const val VPN_PERMISSION_REQUEST_CODE = 1
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2

    @JvmStatic
    var notificationIconResId: Int = 0
        private set

    @JvmStatic
    var notificationChannelId: String = ""
        private set

    /**
     * Sets the notification icon resource ID.
     *
     * @param resId The resource ID of the notification icon
     */
    @JvmStatic
    fun setNotificationIcon(resId: Int) {
        notificationIconResId = resId
    }

    /**
     * Creates the notification channel for the VPN service.
     * Call this once in your Application class or before starting the VPN.
     *
     * @param context The application context
     * @param channelId The notification channel ID
     * @param channelName The notification channel display name
     * @param importance The notification importance level (default: NotificationManager.IMPORTANCE_HIGH)
     */
    @JvmStatic
    @JvmOverloads
    fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_HIGH
    ) {
        notificationChannelId = channelId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d(TAG, "Notification channel created: $channelId")
        }
    }

    /**
     * Checks if VPN permission is granted.
     *
     * @param context The application context
     * @return true if VPN permission is granted, false otherwise
     */
    @JvmStatic
    fun hasVpnPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.BIND_VPN_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if notification permission is granted.
     *
     * @param context The application context
     * @return true if notification permission is granted, false otherwise
     */
    @JvmStatic
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Requests VPN permission.
     *
     * @param activity The activity to request permission from
     * @param callback The callback to be called when permission is granted or denied
     */
    @JvmStatic
    fun requestVpnPermission(
        activity: Activity, callback: (Boolean) -> Unit
    ) {
        val intent = VpnService.prepare(activity)
        if (intent != null) {
            activity.startActivityForResult(intent, VPN_PERMISSION_REQUEST_CODE)
        } else {
            callback(true)
        }
    }

    /**
     * Requests notification permission.
     *
     * @param activity The activity to request permission from
     * @param callback The callback to be called when permission is granted or denied
     */
    @JvmStatic
    fun requestNotificationPermission(
        activity: AppCompatActivity, callback: (Boolean) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val launcher = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                callback(isGranted)
            }
            launcher.launch(POST_NOTIFICATIONS)
        } else {
            callback(true)
        }
    }

    /**
     * Starts the VPN tunnel service with the given configuration.
     *
     * @param context The application context
     * @param config The VPN tunnel configuration
     * @param blockedApps List of blocked apps (optional)
     * @throws IllegalStateException if notification icon or channel is not set
     * @throws IllegalArgumentException if configuration is invalid
     */
    @JvmStatic
    fun startVpnTunnel(context: Context, config: TunnelConfig, blockedApps: List<String>?) {
        require(notificationIconResId != 0) { "Notification icon must be set before starting VPN. Call setNotificationIcon() first." }
        require(notificationChannelId.isNotEmpty()) { "Notification channel must be created before starting VPN. Call createNotificationChannel() first." }

        try {
            val startIntent = Intent(context, TunnelService::class.java).apply {
                putExtra(BLOCKED_APPS, ArrayList(blockedApps ?: emptyList()))
                putExtra(TUNNEL_CONFIG, config)
                putExtra("NOTIFICATION_ICON", notificationIconResId)
            }
            startService(context, startIntent)
            Log.d(TAG, "VPN tunnel service started successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start VPN tunnel service", e)
            throw e
        }
    }

    /**
     * Stops the active VPN tunnel service.
     *
     * @param context The application context
     */
    @JvmStatic
    fun stopVpnTunnel(context: Context) {
        try {
            val stopIntent = Intent(context, TunnelService::class.java).apply {
                action = STOP_ACTION
            }
            startService(context, stopIntent)
            Log.d(TAG, "VPN tunnel service stopped successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop VPN tunnel service", e)
            throw e
        }
    }

    /**
     * Starts the service as a foreground service or a normal service depending on Android version.
     *
     * @param context The application context
     * @param intent The intent to start the service
     * @throws SecurityException if the service cannot be started
     */
    private fun startService(context: Context, intent: Intent) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to start service due to security exception", e)
            throw e
        }
    }
}
