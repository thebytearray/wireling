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

import org.junit.Assert.assertEquals
import org.junit.Test

class TunnelConfigTest {
    @Test
    fun builder_producesValidConfig() {
        val config = TunnelConfig.Builder()
            .setInterfaceAddress("10.0.0.2/24")
            .setPrivateKey("YFAnE0QGjUHqsQ8qUOI6GbmDrdNl2fPULfpQXs0tmW4=")
            .setListenPort(51820)
            .setPublicKey("YFAnE0QGjUHqsQ8qUOI6GbmDrdNl2fPULfpQXs0tmW4=")
            .setAllowedIps(listOf("0.0.0.0/0"))
            .setEndpoint("198.51.100.10:51820")
            .build()

        assertEquals("10.0.0.2/24", config.interfaceField.address)
        assertEquals("198.51.100.10:51820", config.peer.endpoint)
    }

    @Test(expected = IllegalArgumentException::class)
    fun builder_rejectsBadAddress() {
        TunnelConfig.Builder()
            .setInterfaceAddress("not-an-ip")
            .setPrivateKey("YFAnE0QGjUHqsQ8qUOI6GbmDrdNl2fPULfpQXs0tmW4=")
            .setListenPort(51820)
            .setPublicKey("YFAnE0QGjUHqsQ8qUOI6GbmDrdNl2fPULfpQXs0tmW4=")
            .setAllowedIps(listOf("0.0.0.0/0"))
            .setEndpoint("198.51.100.10:51820")
            .build()
    }
}
