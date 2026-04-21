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

package org.thebytearray.wireling.data

import com.wireguard.android.backend.Backend
import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.thebytearray.wireling.domain.TunnelConfig
import org.thebytearray.wireling.domain.VpnTunnelEngine

internal class WireGuardVpnTunnelEngine(
    private val backend: Backend,
    private val tunnel: Tunnel,
) : VpnTunnelEngine {
    override suspend fun connect(
        configuration: TunnelConfig,
        excludedApplicationPackages: List<String>,
    ) = withContext(Dispatchers.IO) {
        val vpnConfig = WireGuardConfigMapper.toWireGuardConfig(configuration, excludedApplicationPackages)
        backend.setState(tunnel, Tunnel.State.UP, vpnConfig)
        Unit
    }

    override suspend fun disconnect() = withContext(Dispatchers.IO) {
        if (backend.getState(tunnel) == Tunnel.State.UP) {
            backend.setState(tunnel, Tunnel.State.DOWN, null)
        }
        Unit
    }

    override fun isTunnelUp(): Boolean =
        try {
            backend.getState(tunnel) == Tunnel.State.UP
        } catch (_: Exception) {
            false
        }
}
