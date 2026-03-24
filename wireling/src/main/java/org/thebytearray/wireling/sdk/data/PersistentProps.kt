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

import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel

internal class PersistentProps private constructor() {
    private var tunnel: Tunnel? = null
    private var backend: GoBackend? = null

    fun getTunnel(): WgTunnel {
        return (tunnel ?: WgTunnel().also { tunnel = it }) as WgTunnel
    }

    fun setBackend(backend: GoBackend?) {
        this.backend = backend
    }

    fun getBackend(): GoBackend {
        return backend ?: throw IllegalStateException("Backend is not initialized")
    }

    internal companion object {
        @Volatile
        private var instance: PersistentProps? = null

        fun getInstance(): PersistentProps {
            return instance ?: synchronized(this) {
                instance ?: PersistentProps().also { instance = it }
            }
        }
    }
}
