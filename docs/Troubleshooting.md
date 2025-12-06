# Troubleshooting

Common issues and their solutions when using WGAndroidLib.

## Common Errors

### "Notification icon must be set before starting VPN"

**Cause:** `ServiceManager.setNotificationIcon()` was not called before starting the VPN.

**Solution:**

```kotlin
// Call this before startVpnTunnel()
ServiceManager.setNotificationIcon(R.drawable.ic_vpn)
```

---

### "Invalid interface address format"

**Cause:** The interface address is not in valid IPv4/CIDR format.

**Valid formats:**
- `10.0.0.2/24`
- `192.168.1.100/32`
- `172.16.0.1/16`

**Invalid formats:**
- `10.0.0.2` (missing CIDR)
- `10.0.0.2/33` (invalid CIDR)
- `256.0.0.1/24` (invalid IP)

---

### "Invalid private key format" / "Invalid public key format"

**Cause:** The WireGuard key is not properly Base64 encoded.

**Valid format:** 44 characters, ending with `=`

```
YHR3e4a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8=
```

**Solution:** Regenerate keys using `wg genkey` and `wg pubkey` commands.

---

### "Invalid endpoint format"

**Cause:** Endpoint is not in `IP:Port` format.

**Valid formats:**
- `192.168.1.1:51820`
- `10.0.0.1:12345`

**Invalid formats:**
- `vpn.example.com:51820` (hostname not supported in validation)
- `192.168.1.1` (missing port)
- `192.168.1.1:` (empty port)

> **Note:** Currently, the library validates IP:Port format only. For hostname support, you may need to resolve the hostname to IP first.

---

### "Invalid listen port"

**Cause:** Port number is outside valid range (1-65535).

**Solution:** Use a valid port number:

```kotlin
.setListenPort(51820)  // Valid
.setListenPort(0)      // Invalid
.setListenPort(70000)  // Invalid
```

---

### VPN Permission Dialog Not Showing

**Possible causes:**

1. **Already granted:** Permission was previously granted
2. **Activity issue:** Not using the correct activity type

**Solution:**

```kotlin
// Check if already granted
if (ServiceManager.hasVpnPermission(this)) {
    // Already have permission, start VPN directly
    startVpn()
} else {
    // Request permission
    ServiceManager.requestVpnPermission(this) { granted ->
        if (granted) startVpn()
    }
}
```

---

### Service Crashes on Start

**Possible causes:**

1. **Missing service declaration** in AndroidManifest.xml
2. **Missing notification channel**
3. **Invalid notification icon**

**Checklist:**

1. Verify AndroidManifest.xml includes both services:

```xml
<service
    android:name="org.thebytearray.wireguard.service.TunnelService"
    android:exported="true"
    android:foregroundServiceType="specialUse"
    android:permission="android.permission.FOREGROUND_SERVICE" />

<service
    android:name="com.wireguard.android.backend.GoBackend$VpnService"
    android:exported="true"
    android:permission="android.permission.BIND_VPN_SERVICE">
    <intent-filter>
        <action android:name="android.net.VpnService" />
    </intent-filter>
</service>
```

2. Verify notification channel is created in Application class
3. Verify notification icon resource exists

---

### Not Receiving State Broadcasts

**Possible causes:**

1. **Receiver not registered**
2. **Wrong intent filter**
3. **Receiver unregistered too early**

**Solution:**

```kotlin
// Use the correct action
val filter = IntentFilter(Constants.STATS_BROADCAST_ACTION)

// Register with RECEIVER_NOT_EXPORTED (Android 13+)
registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)

// Make sure to register in onResume, unregister in onPause
```

---

### VPN Connects But No Internet

**Possible causes:**

1. **Wrong AllowedIPs configuration**
2. **Server-side routing issue**
3. **DNS not configured**

**Solutions:**

1. For full tunnel, use:
```kotlin
.setAllowedIps(listOf("0.0.0.0/0"))
```

2. Verify server is configured to forward traffic
3. Check if your server has proper NAT/masquerade rules

---

### App Excluded from VPN Still Uses VPN

**Cause:** The blockedApps list uses package names, not app names.

**Solution:** Use correct package names:

```kotlin
// Wrong
val excludedApps = listOf("Chrome", "Firefox")

// Correct
val excludedApps = listOf(
    "com.android.chrome",
    "org.mozilla.firefox"
)

ServiceManager.startVpnTunnel(context, config, excludedApps)
```

---

## Debugging Tips

### Enable Logging

The library logs to Android's Logcat with tag `TunnelService` and `ServiceManager`:

```bash
adb logcat -s TunnelService:* ServiceManager:*
```

### Check VPN State

Use ADB to check if VPN is active:

```bash
adb shell dumpsys connectivity | grep -i vpn
```

### Verify Configuration

Print your config before starting:

```kotlin
val config = TunnelConfig.Builder()
    // ... your config
    .build()

Log.d("VPN", "Interface: ${config.interfaceField}")
Log.d("VPN", "Peer: ${config.peer}")
```

---

## Permissions Reference

### Required Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

### Optional Permissions

```xml
<!-- For Android 13+ notification display -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

---

## Getting Help

If you're still experiencing issues:

1. **Check existing issues:** [GitHub Issues](https://github.com/thebytearray/WGAndroidLib/issues)
2. **Create a new issue** with:
   - Android version
   - Device model
   - Library version
   - Complete error log
   - Minimal reproducible example

---

**Back to:** [Home](Home.md) | [API Reference](API-Reference.md)


