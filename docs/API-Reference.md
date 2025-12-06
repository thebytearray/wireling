# API Reference

Complete reference for all public APIs in WGAndroidLib.

## ServiceManager

`org.thebytearray.wireguard.service.ServiceManager`

The main entry point for all VPN operations. This is a singleton object.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `notificationIconResId` | `Int` | The resource ID of the notification icon (read-only) |
| `notificationChannelId` | `String` | The notification channel ID (read-only, default: `"WIREGUARD_CHANNEL"`) |
| `notificationChannelName` | `String` | The notification channel name (read-only, default: `"WireGuard Background Service"`) |

### Methods

#### setNotificationIcon

```kotlin
fun setNotificationIcon(resId: Int)
```

Sets the notification icon for the VPN service. **Must be called before starting the VPN.**

| Parameter | Type | Description |
|-----------|------|-------------|
| `resId` | `Int` | Resource ID of the drawable to use as notification icon |

**Example:**

```kotlin
ServiceManager.setNotificationIcon(R.drawable.ic_vpn)
```

---

#### setNotificationChannel

```kotlin
fun setNotificationChannel(channelId: String, channelName: String)
```

Sets a custom notification channel ID and name. Call this before starting the VPN if you want to use your own notification channel. You must create the notification channel with the same ID in your Application class.

| Parameter | Type | Description |
|-----------|------|-------------|
| `channelId` | `String` | The notification channel ID |
| `channelName` | `String` | The notification channel display name |

**Example:**

```kotlin
// In your Application class, create the channel with your custom ID
val channel = NotificationChannel(
    "MY_VPN_CHANNEL",
    "My VPN Service",
    NotificationManager.IMPORTANCE_HIGH
)
notificationManager.createNotificationChannel(channel)

// Then in your Activity, set the channel before starting VPN
ServiceManager.setNotificationChannel("MY_VPN_CHANNEL", "My VPN Service")
ServiceManager.setNotificationIcon(R.drawable.ic_vpn)
```

---

#### hasVpnPermission

```kotlin
fun hasVpnPermission(context: Context): Boolean
```

Checks if VPN permission has been granted.

| Parameter | Type | Description |
|-----------|------|-------------|
| `context` | `Context` | Application or Activity context |

**Returns:** `true` if VPN permission is granted, `false` otherwise.

**Example:**

```kotlin
if (ServiceManager.hasVpnPermission(context)) {
    // Ready to start VPN
}
```

---

#### hasNotificationPermission

```kotlin
fun hasNotificationPermission(context: Context): Boolean
```

Checks if notification permission has been granted. Always returns `true` for Android 12 and below.

| Parameter | Type | Description |
|-----------|------|-------------|
| `context` | `Context` | Application or Activity context |

**Returns:** `true` if notification permission is granted (or not required), `false` otherwise.

---

#### requestVpnPermission

```kotlin
fun requestVpnPermission(activity: Activity, callback: (Boolean) -> Unit)
```

Requests VPN permission from the user. Shows system VPN permission dialog.

| Parameter | Type | Description |
|-----------|------|-------------|
| `activity` | `Activity` | The activity to launch the permission request from |
| `callback` | `(Boolean) -> Unit` | Callback invoked with `true` if granted, `false` if denied |

**Example:**

```kotlin
ServiceManager.requestVpnPermission(this) { granted ->
    if (granted) {
        // Permission granted
    } else {
        // Permission denied
    }
}
```

---

#### requestNotificationPermission

```kotlin
fun requestNotificationPermission(activity: AppCompatActivity, callback: (Boolean) -> Unit)
```

Requests notification permission (Android 13+). Automatically grants on older versions.

| Parameter | Type | Description |
|-----------|------|-------------|
| `activity` | `AppCompatActivity` | The AppCompatActivity to launch the request from |
| `callback` | `(Boolean) -> Unit` | Callback invoked with permission result |

---

#### startVpnTunnel

```kotlin
fun startVpnTunnel(context: Context, config: TunnelConfig, blockedApps: List<String>?)
```

Starts the VPN tunnel with the specified configuration.

| Parameter | Type | Description |
|-----------|------|-------------|
| `context` | `Context` | Application or Activity context |
| `config` | `TunnelConfig` | The VPN tunnel configuration |
| `blockedApps` | `List<String>?` | Optional list of package names to exclude from VPN |

**Throws:**

- `IllegalStateException` — if notification icon is not set
- `IllegalArgumentException` — if configuration is invalid

**Example:**

```kotlin
val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")
    .setPrivateKey("YOUR_PRIVATE_KEY")
    .setListenPort(51820)
    .setPublicKey("PEER_PUBLIC_KEY")
    .setAllowedIps(listOf("0.0.0.0/0"))
    .setEndpoint("vpn.example.com:51820")
    .build()

// Exclude specific apps from VPN
val excludedApps = listOf("com.example.app1", "com.example.app2")

ServiceManager.startVpnTunnel(this, config, excludedApps)
```

---

#### stopVpnTunnel

```kotlin
fun stopVpnTunnel(context: Context)
```

Stops the active VPN tunnel.

| Parameter | Type | Description |
|-----------|------|-------------|
| `context` | `Context` | Application or Activity context |

**Example:**

```kotlin
ServiceManager.stopVpnTunnel(this)
```

---

## TunnelConfig

`org.thebytearray.wireguard.model.TunnelConfig`

Represents a complete WireGuard tunnel configuration.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `interfaceField` | `Interface` | The interface configuration |
| `peer` | `Peer` | The peer configuration |

### TunnelConfig.Builder

Builder class for creating `TunnelConfig` instances with validation.

#### Methods

| Method | Parameter | Description |
|--------|-----------|-------------|
| `setInterfaceAddress(address: String)` | IP address with CIDR (e.g., "10.0.0.2/24") | Sets the interface IP address |
| `setPrivateKey(key: String)` | Base64-encoded private key | Sets the interface private key |
| `setListenPort(port: Long)` | Port number (1-65535) | Sets the listen port |
| `setPublicKey(key: String)` | Base64-encoded public key | Sets the peer's public key |
| `setAllowedIps(ips: List<String>)` | List of CIDR ranges | Sets allowed IP ranges for the peer |
| `setEndpoint(endpoint: String)` | IP:Port or hostname:Port | Sets the peer endpoint |
| `build()` | — | Creates the TunnelConfig instance |

**Example:**

```kotlin
val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")
    .setPrivateKey("WG_PRIVATE_KEY_BASE64")
    .setListenPort(51820)
    .setPublicKey("PEER_PUBLIC_KEY_BASE64")
    .setAllowedIps(listOf("0.0.0.0/0", "::/0"))  // Route all traffic
    .setEndpoint("192.168.1.1:51820")
    .build()
```

### Validation Rules

The builder validates all inputs:

| Field | Validation |
|-------|------------|
| Interface Address | Valid IPv4 with optional CIDR notation |
| Private Key | Valid Base64 WireGuard key (44 characters ending with `=`) |
| Listen Port | Integer between 1 and 65535 |
| Public Key | Valid Base64 WireGuard key |
| Allowed IPs | List of valid IPv4 addresses with optional CIDR |
| Endpoint | Valid IP:Port format |

---

## TunnelState

`org.thebytearray.wireguard.model.TunnelState`

Enum representing VPN connection states.

| Value | Description |
|-------|-------------|
| `CONNECTED` | VPN tunnel is active and connected |
| `DISCONNECTED` | VPN tunnel is not active |
| `CONNECTING` | VPN tunnel is being established |

---

## Constants

`org.thebytearray.wireguard.util.Constants`

Useful constants for notification and broadcast handling.

### Notification Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `CHANNEL_ID` | `"WIREGUARD_CHANNEL"` | Notification channel ID |
| `CHANNEL_NAME` | `"WireGuard Background Service"` | Notification channel name |
| `FOREGROUND_ID` | `1` | Foreground notification ID |
| `NOTIFICATION_TITLE` | `"WireGuard Connected"` | Default notification title |
| `NOTIFICATION_TEXT` | `"Connected to WireGuard Tunnel"` | Default notification text |

### Broadcast Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `STATS_BROADCAST_ACTION` | `"WIREGUARD_STATS_BROADCAST_ACTION"` | Action for state broadcasts |
| `STATE` | `"STATE"` | Intent extra key for tunnel state |
| `DURATION` | `"DURATION"` | Intent extra key for connection duration |
| `DOWNLOAD_SPEED` | `"DOWNLOAD_SPEED"` | Intent extra key for download speed |
| `UPLOAD_SPEED` | `"UPLOAD_SPEED"` | Intent extra key for upload speed |

### Default Values

| Constant | Value | Description |
|----------|-------|-------------|
| `DEFAULT_STATE` | `TunnelState.DISCONNECTED` | Default tunnel state |
| `DEFAULT_DURATION` | `"00:00:00"` | Default duration string |
| `DEFAULT_DOWNLOAD_SPEED` | `"↓ 00 b/s"` | Default download speed |
| `DEFAULT_UPLOAD_SPEED` | `"↑ 00 b/s"` | Default upload speed |

---

## ServiceListener

`org.thebytearray.wireguard.service.ServiceListener`

Interface for receiving VPN state updates (used internally).

### Methods

```kotlin
fun onStateBroadcast(
    context: Context,
    state: String,
    duration: String,
    downloadSpeed: String,
    uploadSpeed: String
)

fun onVpnDisconnected()
```

---

**Next:** [Configuration Details](Configuration.md) | [State Management](State-Management.md)

