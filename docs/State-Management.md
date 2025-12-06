# State Management

Learn how to monitor VPN connection states and receive real-time updates.

## VPN States

WGAndroidLib uses `TunnelState` enum to represent connection states:

| State | Description |
|-------|-------------|
| `CONNECTING` | VPN connection is being established |
| `CONNECTED` | VPN tunnel is active |
| `DISCONNECTED` | VPN tunnel is not active |

## Receiving State Updates

The library broadcasts VPN state updates using Android's BroadcastReceiver system.

### Register a BroadcastReceiver

#### Kotlin

```kotlin
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import org.thebytearray.wireguard.util.Constants

class MainActivity : AppCompatActivity() {
    
    private val vpnStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val state = it.getStringExtra(Constants.STATE) ?: Constants.DEFAULT_STATE.name
                val duration = it.getStringExtra(Constants.DURATION) ?: Constants.DEFAULT_DURATION
                val downloadSpeed = it.getStringExtra(Constants.DOWNLOAD_SPEED) ?: Constants.DEFAULT_DOWNLOAD_SPEED
                val uploadSpeed = it.getStringExtra(Constants.UPLOAD_SPEED) ?: Constants.DEFAULT_UPLOAD_SPEED
                
                // Update your UI
                updateVpnStatus(state, duration, downloadSpeed, uploadSpeed)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Constants.STATS_BROADCAST_ACTION)
        registerReceiver(vpnStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }
    
    override fun onPause() {
        super.onPause()
        unregisterReceiver(vpnStateReceiver)
    }
    
    private fun updateVpnStatus(
        state: String,
        duration: String,
        downloadSpeed: String,
        uploadSpeed: String
    ) {
        when (state) {
            TunnelState.CONNECTED.name -> {
                tvStatus.text = "Connected"
                tvDuration.text = duration
                tvSpeed.text = "$downloadSpeed • $uploadSpeed"
            }
            TunnelState.CONNECTING.name -> {
                tvStatus.text = "Connecting..."
            }
            TunnelState.DISCONNECTED.name -> {
                tvStatus.text = "Disconnected"
                tvDuration.text = "00:00:00"
                tvSpeed.text = ""
            }
        }
    }
}
```

#### Java

```java
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import org.thebytearray.wireguard.util.Constants;
import org.thebytearray.wireguard.model.TunnelState;

public class MainActivity extends AppCompatActivity {
    
    private final BroadcastReceiver vpnStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String state = intent.getStringExtra(Constants.STATE);
                String duration = intent.getStringExtra(Constants.DURATION);
                String downloadSpeed = intent.getStringExtra(Constants.DOWNLOAD_SPEED);
                String uploadSpeed = intent.getStringExtra(Constants.UPLOAD_SPEED);
                
                updateVpnStatus(state, duration, downloadSpeed, uploadSpeed);
            }
        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Constants.STATS_BROADCAST_ACTION);
        registerReceiver(vpnStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(vpnStateReceiver);
    }
    
    private void updateVpnStatus(String state, String duration, 
                                  String downloadSpeed, String uploadSpeed) {
        if (TunnelState.CONNECTED.name().equals(state)) {
            tvStatus.setText("Connected");
            tvDuration.setText(duration);
            tvSpeed.setText(downloadSpeed + " • " + uploadSpeed);
        } else if (TunnelState.CONNECTING.name().equals(state)) {
            tvStatus.setText("Connecting...");
        } else {
            tvStatus.setText("Disconnected");
            tvDuration.setText("00:00:00");
            tvSpeed.setText("");
        }
    }
}
```

## Broadcast Data

Each broadcast contains the following extras:

| Extra Key | Type | Description | Example |
|-----------|------|-------------|---------|
| `Constants.STATE` | `String` | Current tunnel state | `"CONNECTED"` |
| `Constants.DURATION` | `String` | Connection duration | `"01:23:45"` |
| `Constants.DOWNLOAD_SPEED` | `String` | Current download speed | `"↓ 1.5 MB/s"` |
| `Constants.UPLOAD_SPEED` | `String` | Current upload speed | `"↑ 250 KB/s"` |

## Speed Format

The speed strings are pre-formatted with directional arrows:

- Download: `"↓ X.XX unit/s"` (e.g., `"↓ 1.5 MB/s"`)
- Upload: `"↑ X.XX unit/s"` (e.g., `"↑ 250 KB/s"`)

Units automatically scale: `b/s` → `KB/s` → `MB/s` → `GB/s`

## Duration Format

Duration is formatted as `HH:MM:SS`:

- `"00:00:00"` — Just connected
- `"00:05:30"` — 5 minutes 30 seconds
- `"02:30:00"` — 2 hours 30 minutes

## ViewModel Integration

For MVVM architecture, wrap the broadcast receiver in a ViewModel:

### Kotlin with StateFlow

```kotlin
class VpnViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _vpnState = MutableStateFlow<VpnStatus>(VpnStatus.Disconnected)
    val vpnState: StateFlow<VpnStatus> = _vpnState.asStateFlow()
    
    private val vpnReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                val state = it.getStringExtra(Constants.STATE) ?: return
                val duration = it.getStringExtra(Constants.DURATION) ?: ""
                val download = it.getStringExtra(Constants.DOWNLOAD_SPEED) ?: ""
                val upload = it.getStringExtra(Constants.UPLOAD_SPEED) ?: ""
                
                _vpnState.value = when (state) {
                    TunnelState.CONNECTED.name -> VpnStatus.Connected(duration, download, upload)
                    TunnelState.CONNECTING.name -> VpnStatus.Connecting
                    else -> VpnStatus.Disconnected
                }
            }
        }
    }
    
    init {
        val filter = IntentFilter(Constants.STATS_BROADCAST_ACTION)
        getApplication<Application>().registerReceiver(
            vpnReceiver, filter, Context.RECEIVER_NOT_EXPORTED
        )
    }
    
    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(vpnReceiver)
    }
}

sealed class VpnStatus {
    object Disconnected : VpnStatus()
    object Connecting : VpnStatus()
    data class Connected(
        val duration: String,
        val downloadSpeed: String,
        val uploadSpeed: String
    ) : VpnStatus()
}
```

### Usage in Activity/Fragment

```kotlin
class MainActivity : AppCompatActivity() {
    private val viewModel: VpnViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        lifecycleScope.launch {
            viewModel.vpnState.collect { status ->
                when (status) {
                    is VpnStatus.Connected -> {
                        binding.statusText.text = "Connected"
                        binding.durationText.text = status.duration
                        binding.speedText.text = "${status.downloadSpeed} • ${status.uploadSpeed}"
                    }
                    VpnStatus.Connecting -> {
                        binding.statusText.text = "Connecting..."
                    }
                    VpnStatus.Disconnected -> {
                        binding.statusText.text = "Disconnected"
                    }
                }
            }
        }
    }
}
```

## Jetpack Compose Integration

```kotlin
@Composable
fun VpnStatusCard(viewModel: VpnViewModel = viewModel()) {
    val status by viewModel.vpnState.collectAsState()
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            when (val currentStatus = status) {
                is VpnStatus.Connected -> {
                    Text("Status: Connected", style = MaterialTheme.typography.h6)
                    Text("Duration: ${currentStatus.duration}")
                    Row {
                        Text(currentStatus.downloadSpeed)
                        Spacer(Modifier.width(8.dp))
                        Text(currentStatus.uploadSpeed)
                    }
                }
                VpnStatus.Connecting -> {
                    Text("Status: Connecting...", style = MaterialTheme.typography.h6)
                    CircularProgressIndicator()
                }
                VpnStatus.Disconnected -> {
                    Text("Status: Disconnected", style = MaterialTheme.typography.h6)
                }
            }
        }
    }
}
```

## Best Practices

1. **Register in onResume, unregister in onPause** — Prevents memory leaks
2. **Use RECEIVER_NOT_EXPORTED** — Security best practice for internal broadcasts
3. **Handle null values** — Always provide defaults for intent extras
4. **Update UI on main thread** — BroadcastReceiver callbacks run on main thread by default
5. **Consider using ViewModel** — Survives configuration changes

---

**Next:** [Troubleshooting](Troubleshooting.md) | [API Reference](API-Reference.md)

