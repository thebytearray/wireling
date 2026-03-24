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

import org.thebytearray.wireling.sdk.domain.TunnelState

/**
 * Keys and defaults for connection stats broadcasts sent while the VPN runs.
 *
 * Register a [android.content.BroadcastReceiver] for [STATS_BROADCAST_ACTION] (prefer
 * [androidx.core.content.ContextCompat.registerReceiver] with [androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED]).
 */
public object WireLingConstants {
    /** [android.content.Intent] action for stats updates from the tunnel service. */
    public const val STATS_BROADCAST_ACTION: String = "org.thebytearray.wireling.action.STATS_UPDATE"

    /** String extra: human-readable connection state (typically a [TunnelState] name). */
    public const val EXTRA_STATE: String = "org.thebytearray.wireling.extra.STATE"
    /** String extra: session duration for display. */
    public const val EXTRA_DURATION: String = "org.thebytearray.wireling.extra.DURATION"
    /** String extra: formatted download throughput. */
    public const val EXTRA_DOWNLOAD_SPEED: String = "org.thebytearray.wireling.extra.DOWNLOAD_SPEED"
    /** String extra: formatted upload throughput. */
    public const val EXTRA_UPLOAD_SPEED: String = "org.thebytearray.wireling.extra.UPLOAD_SPEED"

    /** Default [TunnelState] when no update has been received. */
    public val DEFAULT_STATE: TunnelState = TunnelState.DISCONNECTED
    public const val DEFAULT_DURATION: String = "00:00:00"
    public const val DEFAULT_DOWNLOAD_SPEED: String = "↓ 00 b/s"
    public const val DEFAULT_UPLOAD_SPEED: String = "↑ 00 b/s"
}
