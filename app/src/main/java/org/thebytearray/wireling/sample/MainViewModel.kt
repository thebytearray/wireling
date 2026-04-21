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
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.thebytearray.wireling.sample.BuildConfig
import org.thebytearray.wireling.WireLingConstants
import org.thebytearray.wireling.WireLingVpn
import org.thebytearray.wireling.domain.TunnelConfig
import org.thebytearray.wireling.domain.TunnelState

data class WireLingUiState(
    val connectionLabel: String = TunnelState.DISCONNECTED.name,
    val duration: String = WireLingConstants.DEFAULT_DURATION,
    val speeds: String = "",
    val interfaceAddress: String = "",
    val privateKey: String = "",
    val listenPortInput: String = "",
    val publicKey: String = "",
    val allowedIpsInput: String = "",
    val endpoint: String = "",
    val blockedPackagesInput: String = "",
    val lastMessage: String? = null,
    val vpnPrepared: Boolean = false,
    val notificationPrepared: Boolean = false,
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(demoFormDefaults())
    val uiState: StateFlow<WireLingUiState> = _uiState.asStateFlow()

    fun onVpnPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                vpnPrepared = granted,
                lastMessage = if (granted) "VPN permission granted" else "VPN permission denied",
            )
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        _uiState.update {
            it.copy(
                notificationPrepared = granted || it.notificationPrepared,
                lastMessage = if (granted) "Notification permission granted" else "Notification permission denied",
            )
        }
    }

    fun onStatsBroadcast(
        state: String,
        duration: String,
        download: String,
        upload: String,
    ) {
        _uiState.update {
            it.copy(
                connectionLabel = state,
                duration = duration,
                speeds = "$download  •  $upload",
            )
        }
    }

    fun onInterfaceAddressChanged(value: String) {
        _uiState.update { it.copy(interfaceAddress = value) }
    }

    fun onPrivateKeyChanged(value: String) {
        _uiState.update { it.copy(privateKey = value) }
    }

    fun onListenPortChanged(value: String) {
        _uiState.update { it.copy(listenPortInput = value) }
    }

    fun onPublicKeyChanged(value: String) {
        _uiState.update { it.copy(publicKey = value) }
    }

    fun onAllowedIpsChanged(value: String) {
        _uiState.update { it.copy(allowedIpsInput = value) }
    }

    fun onEndpointChanged(value: String) {
        _uiState.update { it.copy(endpoint = value) }
    }

    fun onBlockedPackagesChanged(value: String) {
        _uiState.update { it.copy(blockedPackagesInput = value) }
    }

    fun resetFormToDemoDefaults() {
        _uiState.update {
            demoFormDefaults().copy(
                connectionLabel = it.connectionLabel,
                duration = it.duration,
                speeds = it.speeds,
                lastMessage = "Form reset to demo placeholders (replace with your peer).",
                vpnPrepared = it.vpnPrepared,
                notificationPrepared = it.notificationPrepared,
            )
        }
    }

    fun refreshPermissionSnapshot(context: Context) {
        val vpnOk = WireLingVpn.hasVpnPermission(context)
        val notifOk = WireLingVpn.hasNotificationPermission(context)
        _uiState.update {
            it.copy(
                vpnPrepared = vpnOk,
                notificationPrepared = notifOk,
            )
        }
    }

    fun startTunnel(context: Context) {
        viewModelScope.launch {
            try {
                val config = tunnelConfigFromForm()
                val blocked = _uiState.value.blockedPackagesInput
                    .split(',', ';')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                WireLingVpn.startVpnTunnel(context.applicationContext, config, blocked.ifEmpty { null })
                _uiState.update { it.copy(lastMessage = "Start requested") }
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(lastMessage = e.message ?: "Invalid configuration") }
            } catch (e: IllegalStateException) {
                _uiState.update { it.copy(lastMessage = e.message) }
            } catch (e: Exception) {
                _uiState.update { it.copy(lastMessage = e.message ?: "Start failed") }
            }
        }
    }

    fun stopTunnel(context: Context) {
        viewModelScope.launch {
            try {
                WireLingVpn.stopVpnTunnel(context.applicationContext)
                _uiState.update { it.copy(lastMessage = "Stop requested") }
            } catch (e: Exception) {
                _uiState.update { it.copy(lastMessage = e.message ?: "Stop failed") }
            }
        }
    }

    private fun tunnelConfigFromForm(): TunnelConfig {
        val s = _uiState.value
        val port = s.listenPortInput.trim().toLongOrNull()
            ?: throw IllegalArgumentException("Listen port must be a number (1–65535).")
        val allowed = s.allowedIpsInput
            .split(',', ';', '\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (allowed.isEmpty()) {
            throw IllegalArgumentException("Enter at least one allowed IP (e.g. 0.0.0.0/0).")
        }
        return TunnelConfig.Builder()
            .setInterfaceAddress(s.interfaceAddress.trim())
            .setPrivateKey(s.privateKey.trim())
            .setListenPort(port)
            .setPublicKey(s.publicKey.trim())
            .setAllowedIps(allowed)
            .setEndpoint(s.endpoint.trim())
            .build()
    }

    private companion object {
        fun demoFormDefaults(): WireLingUiState = WireLingUiState(
            interfaceAddress = BuildConfig.DEMO_INTERFACE_ADDRESS,
            privateKey = BuildConfig.DEMO_WG_PRIVATE_KEY,
            listenPortInput = BuildConfig.DEMO_LISTEN_PORT.toString(),
            publicKey = BuildConfig.DEMO_WG_PUBLIC_KEY,
            allowedIpsInput = "0.0.0.0/0",
            endpoint = BuildConfig.DEMO_ENDPOINT,
        )
    }
}
