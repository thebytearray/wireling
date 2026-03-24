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

package org.thebytearray.wireling.sdk.domain

/** High-level VPN tunnel lifecycle state surfaced to the app (e.g. via stats broadcasts). */
public enum class TunnelState {
    /** Tunnel is up and passing traffic. */
    CONNECTED,
    /** Tunnel is down. */
    DISCONNECTED,
    /** Tunnel is being established. */
    CONNECTING,
}
