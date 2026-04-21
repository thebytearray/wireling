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

package org.thebytearray.wireling.platform

import android.app.Service
import android.content.Intent
import android.net.TrafficStats
import android.os.IBinder
import android.util.Log
import androidx.core.content.IntentCompat
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.thebytearray.wireling.WireLingVpn
import org.thebytearray.wireling.data.PersistentProps
import org.thebytearray.wireling.data.WireGuardVpnTunnelEngine
import org.thebytearray.wireling.domain.TunnelConfig
import org.thebytearray.wireling.domain.TunnelState
import org.thebytearray.wireling.domain.VpnTunnelEngine
import kotlin.coroutines.CoroutineContext

/**
 * Foreground [Service] that owns the WireGuard tunnel, notifications, and stats broadcasts.
 * Started via [org.thebytearray.wireling.WireLingVpn.startVpnTunnel]; not intended for direct use.
 */
public class TunnelService : Service(), CoroutineScope {
    private val tag = "WireLingTunnelService"

    private lateinit var foregroundNotifier: WireLingForegroundNotifier
    private lateinit var tunnelEngine: VpnTunnelEngine
    private lateinit var backendTunnel: Tunnel
    private var statsListener: ServiceListener? = null

    private var uptimeSeconds: Long = 0
    private var lastRxBytes: Long = TrafficStats.getTotalRxBytes().takeIf { it >= 0 } ?: 0
    private var lastTxBytes: Long = TrafficStats.getTotalTxBytes().takeIf { it >= 0 } ?: 0
    private var isVpnActive: Boolean = false
    private var vpnState: TunnelState = TunnelState.DISCONNECTED
    private var vpnJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + Job()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        try {
            foregroundNotifier = WireLingForegroundNotifier(this, WireLingVpn.notificationIconResId)
            statsListener = foregroundNotifier
            initializeEngine()
            startForeground(
                TunnelIntents.FOREGROUND_NOTIFICATION_ID,
                foregroundNotifier.createNotification().build(),
            )
            Log.d(tag, "TunnelService created successfully")
        } catch (e: Exception) {
            Log.e(tag, "Failed to create TunnelService", e)
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            try {
                when (it.action) {
                    TunnelIntents.STOP_ACTION, TunnelIntents.DISCONNECT_ACTION -> {
                        handleVpnStopOrDisconnect()
                        return START_NOT_STICKY
                    }
                    else -> handleVpnStart(it)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error handling intent action: ${it.action}", e)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        return START_STICKY
    }

    private fun handleVpnStopOrDisconnect() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                disconnectVpn()
                stopSelf()
            } catch (e: Exception) {
                Log.e(tag, "Error stopping VPN", e)
                stopSelf()
            }
        }
    }

    private fun handleVpnStart(intent: Intent) {
        val blockedApps = intent.getStringArrayListExtra(TunnelIntents.BLOCKED_APPS) ?: emptyList()
        if (!isVpnActive) {
            val config = readTunnelConfig(intent)
            if (config != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        connectVpn(config, blockedApps)
                    } catch (e: Exception) {
                        Log.e(tag, "Error connecting VPN", e)
                        stopSelf()
                    }
                }
            } else {
                Log.e(tag, "Invalid tunnel configuration")
                stopSelf()
            }
        } else {
            vpnJob = launch(Dispatchers.IO) {
                try {
                    disconnectVpn()
                } catch (e: Exception) {
                    Log.e(tag, "Error disconnecting VPN", e)
                    stopSelf()
                }
            }
        }
    }

    private fun readTunnelConfig(intent: Intent): TunnelConfig? {
        return IntentCompat.getSerializableExtra(
            intent,
            TunnelIntents.TUNNEL_CONFIG,
            TunnelConfig::class.java,
        )
    }

    private fun initializeEngine() {
        try {
            PersistentProps.Companion.getInstance().setBackend(GoBackend(this))
            val backend = PersistentProps.Companion.getInstance().getBackend()
            backendTunnel = PersistentProps.Companion.getInstance().getTunnel()
            tunnelEngine = WireGuardVpnTunnelEngine(backend, backendTunnel)
            Log.d(tag, "Tunnel engine initialized successfully")
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize tunnel engine", e)
            throw e
        }
    }

    private suspend fun disconnectVpn() {
        try {
            withContext(Dispatchers.IO) {
                tunnelEngine.disconnect()
            }
            isVpnActive = false
            stopVpnTimer()
            sendDisconnectBroadcast()
            Log.d(tag, "VPN disconnected successfully")
        } catch (e: Exception) {
            Log.e(tag, "Error disconnecting VPN", e)
            throw e
        }
    }

    private suspend fun connectVpn(config: TunnelConfig, blockedApps: List<String>) {
        Log.d(tag, "Connecting to VPN: $config")
        vpnState = TunnelState.CONNECTING

        try {
            tunnelEngine.connect(config, blockedApps)
            vpnState = TunnelState.CONNECTED
            isVpnActive = true
            startVpnTimer()
            broadcastVpnState()
            Log.d(tag, "VPN connected successfully")
        } catch (e: Exception) {
            vpnState = TunnelState.DISCONNECTED
            Log.e(tag, "Error connecting VPN", e)
            throw e
        }
    }

    private fun broadcastVpnState() {
        try {
            val currentRxBytes = TrafficStats.getTotalRxBytes().takeIf { it >= 0 } ?: lastRxBytes
            val currentTxBytes = TrafficStats.getTotalTxBytes().takeIf { it >= 0 } ?: lastTxBytes

            vpnState = try {
                if (tunnelEngine.isTunnelUp()) TunnelState.CONNECTED else TunnelState.DISCONNECTED
            } catch (e: Exception) {
                Log.e(tag, "Error getting tunnel state", e)
                TunnelState.DISCONNECTED
            }

            val duration = uptimeSeconds.formatDuration()
            val downloadSpeed = "↓${(currentRxBytes - lastRxBytes).toSpeedString()}"
            val uploadSpeed = "↑${(currentTxBytes - lastTxBytes).toSpeedString()}"

            statsListener?.onStateBroadcast(this, vpnState.name, duration, downloadSpeed, uploadSpeed)
            foregroundNotifier.updateNotification(
                TunnelIntents.FOREGROUND_NOTIFICATION_ID,
                downloadSpeed,
                uploadSpeed,
            )

            lastRxBytes = currentRxBytes
            lastTxBytes = currentTxBytes
        } catch (e: Exception) {
            Log.e(tag, "Error broadcasting VPN state", e)
        }
    }

    private fun sendDisconnectBroadcast() {
        try {
            statsListener?.onVpnDisconnected()
        } catch (e: Exception) {
            Log.e(tag, "Error sending disconnect broadcast", e)
        }
    }

    private fun stopVpnTimer() {
        try {
            uptimeSeconds = 0
            isVpnActive = false
            vpnJob?.cancel()
            vpnJob = null
            broadcastVpnState()
        } catch (e: Exception) {
            Log.e(tag, "Error stopping VPN timer", e)
        }
    }

    private fun startVpnTimer() {
        vpnJob = launch(Dispatchers.IO) {
            try {
                while (isVpnActive) {
                    uptimeSeconds++
                    broadcastVpnState()
                    delay(1000)
                }
            } catch (e: Exception) {
                Log.e(tag, "Error in VPN timer", e)
            }
        }
    }

    override fun onDestroy() {
        try {
            if (::tunnelEngine.isInitialized) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        tunnelEngine.disconnect()
                    } catch (e: Exception) {
                        Log.e(tag, "Error disconnecting on destroy", e)
                    }
                }
            }
            vpnJob?.cancel()
            if (::foregroundNotifier.isInitialized) {
                foregroundNotifier.cancelNotification(TunnelIntents.FOREGROUND_NOTIFICATION_ID)
            }
            Log.d(tag, "TunnelService destroyed successfully")
        } catch (e: Exception) {
            Log.e(tag, "Error destroying TunnelService", e)
        } finally {
            super.onDestroy()
        }
    }
}
