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

package org.thebytearray.wireling.domain

/**
 * Abstraction over the underlying WireGuard backend; implemented inside the library.
 * Apps normally use [org.thebytearray.wireling.WireLingVpn] instead of this type.
 */
public interface VpnTunnelEngine {
    /**
     * Brings the tunnel up with [configuration].
     *
     * @param excludedApplicationPackages App package names to exclude from the VPN (split/exclude semantics depend on backend mapping).
     */
    public suspend fun connect(configuration: TunnelConfig, excludedApplicationPackages: List<String>)

    /** Tears down the tunnel. */
    public suspend fun disconnect()

    /** Whether the tunnel is currently considered active. */
    public fun isTunnelUp(): Boolean
}
