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

package org.thebytearray.wireling.sdk.data

import com.wireguard.config.Config
import com.wireguard.config.InetEndpoint
import com.wireguard.config.InetNetwork
import com.wireguard.config.Interface
import com.wireguard.config.Peer
import org.thebytearray.wireling.sdk.domain.TunnelConfig

internal object WireGuardConfigMapper {
    fun toWireGuardConfig(configuration: TunnelConfig, excludedApplicationPackages: List<String>): Config {
        val interfaceBuilder = Interface.Builder().apply {
            addAddress(InetNetwork.parse(configuration.interfaceField.address))
                .excludeApplications(excludedApplicationPackages)
            parsePrivateKey(configuration.interfaceField.privateKey)
            setMtu(1420)
        }

        val peerBuilder = Peer.Builder().apply {
            configuration.peer.allowedIps.forEach { addAllowedIp(InetNetwork.parse(it.trim())) }
            setEndpoint(InetEndpoint.parse(configuration.peer.endpoint))
            parsePublicKey(configuration.peer.publicKey)
        }

        return Config.Builder()
            .setInterface(interfaceBuilder.build())
            .addPeer(peerBuilder.build())
            .build()
    }
}
