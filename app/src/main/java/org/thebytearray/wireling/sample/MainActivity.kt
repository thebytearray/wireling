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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.thebytearray.wireling.sdk.WireLingConstants
import org.thebytearray.wireling.sdk.WireLingVpn
import org.thebytearray.wireling.ui.theme.WireLingTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var vpnPermissionLauncher: ActivityResultLauncher<Intent>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vpnPermissionLauncher = WireLingVpn.registerVpnPermissionLauncher(this) { granted: Boolean ->
            viewModel.onVpnPermissionResult(granted)
        }
        notificationPermissionLauncher = WireLingVpn.registerPostNotificationsLauncher(this) { granted: Boolean ->
            viewModel.onNotificationPermissionResult(granted)
        }
        enableEdgeToEdge()
        setContent {
            WireLingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WireLingSampleRoute(
                        viewModel = viewModel,
                        onRequestVpnPermission = {
                            WireLingVpn.launchVpnPermissionFlow(
                                vpnPermissionLauncher,
                                WireLingVpn.vpnPermissionPreparationIntent(this),
                            ) {
                                viewModel.onVpnPermissionResult(true)
                            }
                        },
                        onRequestNotificationPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                WireLingVpn.launchPostNotificationsRequest(notificationPermissionLauncher)
                            } else {
                                viewModel.onNotificationPermissionResult(true)
                            }
                        },
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPermissionSnapshot(this)
    }
}

@Composable
private fun WireLingSampleRoute(
    viewModel: MainViewModel,
    onRequestVpnPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                intent ?: return
                val state = intent.getStringExtra(WireLingConstants.EXTRA_STATE) ?: return
                val duration = intent.getStringExtra(WireLingConstants.EXTRA_DURATION)
                    ?: WireLingConstants.DEFAULT_DURATION
                val down = intent.getStringExtra(WireLingConstants.EXTRA_DOWNLOAD_SPEED)
                    ?: WireLingConstants.DEFAULT_DOWNLOAD_SPEED
                val up = intent.getStringExtra(WireLingConstants.EXTRA_UPLOAD_SPEED)
                    ?: WireLingConstants.DEFAULT_UPLOAD_SPEED
                viewModel.onStatsBroadcast(state, duration, down, up)
            }
        }
        val filter = IntentFilter(WireLingConstants.STATS_BROADCAST_ACTION)
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("WireLing sample", style = MaterialTheme.typography.headlineSmall)
        Text("State: ${ui.connectionLabel}")
        Text("Duration: ${ui.duration}")
        Text("Speeds: ${ui.speeds}")
        ui.lastMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        Button(onClick = onRequestVpnPermission, modifier = Modifier.fillMaxWidth()) {
            Text("Request VPN permission")
        }
        Button(onClick = onRequestNotificationPermission, modifier = Modifier.fillMaxWidth()) {
            Text("Request notification permission (Android 13+)")
        }
        Button(
            onClick = { viewModel.startTunnel(context) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Start tunnel")
        }
        Button(
            onClick = { viewModel.stopTunnel(context) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Stop tunnel")
        }

        Text("Tunnel configuration", style = MaterialTheme.typography.titleMedium)
        Text(
            "Endpoint must be IPv4:port (e.g. 203.0.113.1:51820). Keys: 44-character Base64 ending with =.",
            style = MaterialTheme.typography.bodySmall,
        )

        OutlinedTextField(
            value = ui.interfaceAddress,
            onValueChange = viewModel::onInterfaceAddressChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Interface address (CIDR)") },
            singleLine = true,
        )
        OutlinedTextField(
            value = ui.privateKey,
            onValueChange = viewModel::onPrivateKeyChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Your private key") },
            singleLine = false,
        )
        OutlinedTextField(
            value = ui.listenPortInput,
            onValueChange = viewModel::onListenPortChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Listen port (UDP)") },
            singleLine = true,
        )
        OutlinedTextField(
            value = ui.publicKey,
            onValueChange = viewModel::onPublicKeyChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Peer public key") },
            singleLine = false,
        )
        OutlinedTextField(
            value = ui.allowedIpsInput,
            onValueChange = viewModel::onAllowedIpsChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Allowed IPs (comma-separated)") },
            singleLine = false,
        )
        OutlinedTextField(
            value = ui.endpoint,
            onValueChange = viewModel::onEndpointChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Peer endpoint (IPv4:port)") },
            singleLine = true,
        )

        OutlinedTextField(
            value = ui.blockedPackagesInput,
            onValueChange = viewModel::onBlockedPackagesChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Excluded packages (comma-separated)") },
            singleLine = false,
        )

        Button(
            onClick = viewModel::resetFormToDemoDefaults,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Reset form to demo placeholders")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun WireLingSamplePreview() {
    WireLingTheme {
        Text("WireLing")
    }
}
