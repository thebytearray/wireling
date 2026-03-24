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

import java.io.Serializable
import java.util.regex.Pattern

/**
 * Serializable WireGuard-style tunnel description: local [interfaceField] and remote [peer].
 *
 * Validation runs in `init`; use [Builder] to construct. Endpoint must match `IPv4:port` (see [Peer.endpoint]).
 */
public data class TunnelConfig(
    /** Local tunnel interface (address, private key, listen port). */
    val interfaceField: Interface,
    /** Remote peer (public key, allowed IPs, endpoint). */
    val peer: Peer,
) : Serializable {
    init {
        require(interfaceField.address.isValidIpAddress()) { "Invalid interface address format" }
        require(interfaceField.privateKey.isValidWireGuardKey()) { "Invalid private key format" }
        require(interfaceField.listenPort in 1..65535) { "Invalid listen port" }
        require(peer.publicKey.isValidWireGuardKey()) { "Invalid public key format" }
        require(peer.allowedIps.all { it.isValidIpAddress() }) { "Invalid allowed IPs format" }
        require(peer.endpoint.isValidEndpoint()) { "Invalid endpoint format" }
    }

    /** Fluent builder for [TunnelConfig]; all fields are required before [build]. */
    public class Builder {
        private var interfaceAddress: String? = null
        private var privateKey: String? = null
        private var listenPort: Long = 0
        private var publicKey: String? = null
        private var allowedIps: List<String> = emptyList()
        private var endpoint: String? = null

        /** IPv4 address with CIDR, e.g. `10.0.0.2/24`. */
        public fun setInterfaceAddress(address: String): Builder = apply { this.interfaceAddress = address }
        /** This device’s WireGuard private key (Base64, 44 chars ending with `=`). */
        public fun setPrivateKey(key: String): Builder = apply { this.privateKey = key }
        /** Local UDP listen port; must be in `1..65535` for a valid config. */
        public fun setListenPort(port: Long): Builder = apply { this.listenPort = port }
        /** Peer’s WireGuard public key (Base64, 44 chars ending with `=`). */
        public fun setPublicKey(key: String): Builder = apply { this.publicKey = key }
        /** Routes to send through the tunnel; must be non-empty (e.g. `0.0.0.0/0`). */
        public fun setAllowedIps(ips: List<String>): Builder = apply { this.allowedIps = ips }
        /** Peer `host:port`; currently validated as IPv4 and port only. */
        public fun setEndpoint(endpoint: String): Builder = apply { this.endpoint = endpoint }

        /**
         * Validates and returns an immutable [TunnelConfig].
         *
         * @throws IllegalArgumentException if any field is missing or fails format checks.
         */
        public fun build(): TunnelConfig {
            requireNotNull(interfaceAddress) { "Interface address is required" }
            requireNotNull(privateKey) { "Private key is required" }
            requireNotNull(publicKey) { "Public key is required" }
            requireNotNull(endpoint) { "Endpoint is required" }
            require(allowedIps.isNotEmpty()) { "At least one allowed IP is required" }

            return TunnelConfig(
                interfaceField = Interface(
                    address = interfaceAddress!!,
                    privateKey = privateKey!!,
                    listenPort = listenPort,
                ),
                peer = Peer(
                    publicKey = publicKey!!,
                    allowedIps = allowedIps,
                    endpoint = endpoint!!,
                ),
            )
        }
    }

    private companion object {
        private val IP_PATTERN = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(/([0-9]|[1-2][0-9]|3[0-2]))?$",
        )
        private val WG_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9+/]{43}=$")
        private val ENDPOINT_PATTERN = Pattern.compile(
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]):[0-9]+$",
        )

        private fun String.isValidIpAddress(): Boolean = IP_PATTERN.matcher(this).matches()
        private fun String.isValidWireGuardKey(): Boolean = WG_KEY_PATTERN.matcher(this).matches()
        private fun String.isValidEndpoint(): Boolean = ENDPOINT_PATTERN.matcher(this).matches()
    }
}

/** Local WireGuard interface parameters. */
public data class Interface(
    /** IPv4 with CIDR. */
    val address: String,
    val privateKey: String,
    /** UDP port in `1..65535`. */
    val listenPort: Long,
) : Serializable

/** Remote WireGuard peer parameters. */
public data class Peer(
    val publicKey: String,
    val allowedIps: List<String>,
    /** `IPv4:port` (hostname not accepted by current validation). */
    val endpoint: String,
) : Serializable
