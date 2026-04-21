# Quick start

## 1. Notification channel (Application)

```kotlin
import org.thebytearray.wireling.WireLingVpn

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WireLingVpn.createNotificationChannel(
            context = this,
            channelId = "wireling_vpn",
            channelName = "VPN",
        )
    }
}
```

Register `android:name` in the manifest.

## 2. Notification icon (before connect)

```kotlin
WireLingVpn.setNotificationIcon(R.drawable.ic_vpn)
```

## 3. VPN permission (Activity Result)

```kotlin
val vpnLauncher = WireLingVpn.registerVpnPermissionLauncher(this) { granted -> /* ... */ }

WireLingVpn.launchVpnPermissionFlow(
    launcher = vpnLauncher,
    preparationIntent = WireLingVpn.vpnPermissionPreparationIntent(this),
    onAlreadyGranted = { /* start */ },
)
```

## 4. Notifications permission (API 33+)

```kotlin
val notifLauncher = WireLingVpn.registerPostNotificationsLauncher(this) { granted -> /* ... */ }
WireLingVpn.launchPostNotificationsRequest(notifLauncher)
```

## 5. Connect

```kotlin
import org.thebytearray.wireling.domain.TunnelConfig

val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")
    .setPrivateKey("YOUR_PRIVATE_KEY_BASE64=")
    .setListenPort(51820L)
    .setPublicKey("PEER_PUBLIC_KEY_BASE64=")
    .setAllowedIps(listOf("0.0.0.0/0"))
    .setEndpoint("203.0.113.1:51820") // must match library validation: IPv4:port
    .build()

WireLingVpn.startVpnTunnel(context, config, blockedPackagesOrNull)
```

## 6. Disconnect

```kotlin
WireLingVpn.stopVpnTunnel(context)
```

The **`app`** module in this repository is a full Compose sample: editable fields, permissions, start/stop.

---

**Next:** [API Reference](API-Reference.md) · [Configuration](Configuration.md)
