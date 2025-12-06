# WGAndroidLib - Simple Android WireGuard Library

[![CI](https://github.com/thebytearray/WGAndroidLib/actions/workflows/ci.yml/badge.svg)](https://github.com/thebytearray/WGAndroidLib/actions/workflows/ci.yml)
[![](https://jitpack.io/v/thebytearray/WGAndroidLib.svg)](https://jitpack.io/#thebytearray/WGAndroidLib)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

A wrapper around the official [wireguard-android](https://github.com/WireGuard/wireguard-android) project, designed to simplify VPN implementation in Android apps without dealing with complex low-level functions.

**Production ready** with simple APIs and easy abstraction layer.

## Documentation

For comprehensive documentation, see the **[Wiki](docs/Home.md)**:

| Guide | Description |
|-------|-------------|
| [Installation](docs/Installation.md) | Add the library to your project |
| [Quick Start](docs/Quick-Start.md) | Get up and running in minutes |
| [API Reference](docs/API-Reference.md) | Complete API documentation |
| [Configuration](docs/Configuration.md) | TunnelConfig and validation details |
| [State Management](docs/State-Management.md) | Handle VPN states and broadcasts |
| [Troubleshooting](docs/Troubleshooting.md) | Common issues and solutions |

## Features

- **Developer-Friendly APIs** — Simple methods to start, stop, and monitor VPN connections
- **State Management** — Built-in broadcast receiver for real-time connection state updates
- **Notification System** — Automatic foreground service notifications with speed statistics
- **Configuration Validation** — Built-in validation for WireGuard configurations
- **Permission Handling** — Easy-to-use permission request APIs for VPN and notifications

## Quick Installation

### Option 1: JitPack (Recommended)

Add JitPack repository and the dependency:

**Groovy:**

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
 implementation 'com.github.thebytearray:WGAndroidLib:<latest-version>'
}
```

**Kotlin DSL:**

```kotlin
repositories {
    ...
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.thebytearray:WGAndroidLib:<latest-version>")
}
```

Sync the project and follow the setup guide below.

### Option 2: Git Submodule

For advanced users who want to customize the library:

**1. Clone the repository:**

```
git clone https://github.com/thebytearray/WGAndroidLib.git
```

**2. In Android Studio:** Go to **File → New → Import Module**, select the `wireguard` directory.

**3. Add module dependency:**

**Groovy:**

```groovy
dependencies {
    implementation project(':wireguard')
}
```

**Kotlin DSL:**

```kotlin
dependencies {
    implementation(project(":wireguard"))
}
```

## Quick Setup

### 1. Create Notification Channel

Configure your Application class to create a notification channel:

**Kotlin:**

```kotlin
class TunnelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Create notification channel with your own ID, name, and importance
        ServiceManager.createNotificationChannel(
            context = this,
            channelId = "vpn_channel",
            channelName = "VPN Service",
            importance = NotificationManager.IMPORTANCE_HIGH
        )
    }
}
```

**Java:**

```java
public class TunnelApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Create notification channel with your own ID, name, and importance
        ServiceManager.createNotificationChannel(
            this,
            "vpn_channel",
            "VPN Service",
            NotificationManager.IMPORTANCE_HIGH
        );
    }
}
```
### 2. Configure AndroidManifest.xml

Add permissions and service declarations:
```xml
<!-- Permissions -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Services -->
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

### 3. Initialize and Use

**Kotlin:**
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set notification icon -> for the service :
        ServiceManager.setNotificationIcon(R.drawable.ic_vpn)
        
        // Check and request permissions if not granted
        if (!ServiceManager.hasVpnPermission(this)) {
            ServiceManager.requestVpnPermission(this) { isGranted ->
                if (isGranted) {
                    // VPN permission granted -> you can start now safely
                }
            }
        }
        
        if (!ServiceManager.hasNotificationPermission(this)) {
            ServiceManager.requestNotificationPermission(this) { isGranted ->
                if (isGranted) {
                    // Notification permission granted ,optional though
                }
            }
        }
    }
    
    private fun startVpn() {
        // Create VPN configuration using builder pattern
        val config = TunnelConfig.Builder()
            .setInterfaceAddress("10.0.0.2/24")
            .setPrivateKey("YOUR_PRIVATE_KEY")
            .setListenPort(51820)
            .setPublicKey("PEER_PUBLIC_KEY")
            .setAllowedIps(listOf("0.0.0.0/0"))
            .setEndpoint("PEER_ENDPOINT:51820")
            .build()
            
        // Start VPN with configuration
        ServiceManager.startVpnTunnel(this, config, null)
    }
    
    private fun stopVpn() {
        ServiceManager.stopVpnTunnel(this)
    }
}
```

**Java:**

```java
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set notification icon -> for the service :
        ServiceManager.setNotificationIcon(R.drawable.ic_vpn);
        
        // Check and request permissions if not granted
        if (!ServiceManager.hasVpnPermission(this)) {
            ServiceManager.requestVpnPermission(this, isGranted -> {
                if (isGranted) {
                    // VPN permission granted -> you can start now safely
                }
            });
        }
        
        if (!ServiceManager.hasNotificationPermission(this)) {
            ServiceManager.requestNotificationPermission(this, isGranted -> {
                if (isGranted) {
                    // Notification permission granted
                }
            });
        }
    }
    
    private void startVpn() {
        // Create VPN configuration using builder pattern
        TunnelConfig config = new TunnelConfig.Builder()
            .setInterfaceAddress("10.0.0.2/24")
            .setPrivateKey("YOUR_PRIVATE_KEY")
            .setListenPort(51820)
            .setPublicKey("PEER_PUBLIC_KEY")
            .setAllowedIps(Collections.singletonList("0.0.0.0/0"))
            .setEndpoint("PEER_ENDPOINT:51820")
            .build();
            
        // Start VPN with configuration
        ServiceManager.startVpnTunnel(this, config, null);
    }
    
    private void stopVpn() {
        ServiceManager.stopVpnTunnel(this);
    }
}
```
## Error Handling

The library handles common errors automatically, particularly configuration validation. For custom error handling, import the library as a module (Option 2).

See the [Troubleshooting Guide](docs/Troubleshooting.md) for common issues and solutions.

## License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)** - See [LICENSE](LICENSE) for details.

## Acknowledgements

- [wireguard-android](https://github.com/WireGuard/wireguard-android) — Official WireGuard Android implementation

## Trademark Notice

"WireGuard" and the "WireGuard" logo are registered trademarks of Jason A. Donenfeld.

---

*Maintained by [TheByteArray](https://thebytearray.org)*

