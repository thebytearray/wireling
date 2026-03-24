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

package org.thebytearray.wireling.sdk.platform

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import org.thebytearray.wireling.sdk.WireLingConstants
import org.thebytearray.wireling.sdk.WireLingVpn

internal class WireLingForegroundNotifier(
    private val context: Context,
    private val notificationIconResId: Int,
    private val channelId: String = WireLingVpn.notificationChannelId,
) : ServiceListener {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotification(
        title: String = NOTIFICATION_TITLE,
        contentText: String = NOTIFICATION_TEXT,
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(notificationIconResId)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentTitle(title)
            .setContentText(contentText)
            .addAction(
                NotificationCompat.Action(
                    notificationIconResId,
                    "Disconnect",
                    getDisconnectPendingIntent(),
                ),
            )
    }

    fun updateNotification(notificationId: Int, downloadSpeed: String, uploadSpeed: String) {
        val contentText = "$downloadSpeed • $uploadSpeed"
        val notification = createNotification(contentText = contentText)
        notificationManager.notify(notificationId, notification.build())
    }

    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    private fun getDisconnectPendingIntent(): PendingIntent {
        val intent = Intent(context, TunnelService::class.java).apply {
            action = TunnelIntents.DISCONNECT_ACTION
        }
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    override fun onStateBroadcast(
        context: Context,
        state: String,
        duration: String,
        downloadSpeed: String,
        uploadSpeed: String,
    ) {
        val intent = Intent(WireLingConstants.STATS_BROADCAST_ACTION).apply {
            putExtra(WireLingConstants.EXTRA_STATE, state)
            putExtra(WireLingConstants.EXTRA_DURATION, duration)
            putExtra(WireLingConstants.EXTRA_DOWNLOAD_SPEED, downloadSpeed)
            putExtra(WireLingConstants.EXTRA_UPLOAD_SPEED, uploadSpeed)
        }
        context.sendBroadcast(intent)
    }

    override fun onVpnDisconnected() {
        val intent = Intent(WireLingConstants.STATS_BROADCAST_ACTION).apply {
            putExtra(WireLingConstants.EXTRA_STATE, WireLingConstants.DEFAULT_STATE.name)
            putExtra(WireLingConstants.EXTRA_DURATION, WireLingConstants.DEFAULT_DURATION)
            putExtra(WireLingConstants.EXTRA_DOWNLOAD_SPEED, WireLingConstants.DEFAULT_DOWNLOAD_SPEED)
            putExtra(WireLingConstants.EXTRA_UPLOAD_SPEED, WireLingConstants.DEFAULT_UPLOAD_SPEED)
        }
        context.sendBroadcast(intent)
    }

    private companion object {
        const val NOTIFICATION_TITLE: String = "WireLing connected"
        const val NOTIFICATION_TEXT: String = "Tunnel active"
    }
}
