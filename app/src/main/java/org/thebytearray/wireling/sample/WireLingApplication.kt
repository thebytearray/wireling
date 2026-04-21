/*
 WireLing
 https://github.com/thebytearray/wireling

 Created by Tamim Hossain.
 Copyright (c) 2025 The Byte Array LTD.

 This file is part of the WireLing sample application.

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

package org.thebytearray.wireling.sample

import android.app.Application
import android.app.NotificationManager
import org.thebytearray.wireling.sample.R
import org.thebytearray.wireling.WireLingVpn

class WireLingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WireLingVpn.setNotificationIcon(R.drawable.ic_wireling_vpn)
        WireLingVpn.createNotificationChannel(
            context = this,
            channelId = "wireling_vpn",
            channelName = getString(R.string.notification_channel_name),
            importance = NotificationManager.IMPORTANCE_HIGH,
        )
    }
}
