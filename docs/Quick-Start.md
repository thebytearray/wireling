# Quick Start Guide

Get your VPN up and running in just a few steps.

## Step 1: Create Notification Channel

WGAndroidLib uses a foreground service that requires a notification channel. Create it in your `Application` class:

### Kotlin

```kotlin
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import org.thebytearray.wireguard.util.Constants.CHANNEL_ID
import org.thebytearray.wireguard.util.Constants.CHANNEL_NAME

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }
}
```

### Java

```java
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import static org.thebytearray.wireguard.util.Constants.CHANNEL_ID;
import static org.thebytearray.wireguard.util.Constants.CHANNEL_NAME;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
```

Don't forget to register your Application class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ... >
```

## Step 2: Set Notification Icon

Before starting the VPN, set the notification icon (usually in your main activity):

### Kotlin

```kotlin
import org.thebytearray.wireguard.service.ServiceManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set notification icon (required before starting VPN)
        ServiceManager.setNotificationIcon(R.drawable.ic_vpn)
    }
}
```

### Java

```java
import org.thebytearray.wireguard.service.ServiceManager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set notification icon (required before starting VPN)
        ServiceManager.INSTANCE.setNotificationIcon(R.drawable.ic_vpn);
    }
}
```

## Step 3: Request Permissions

Request VPN and notification permissions:

### Kotlin

```kotlin
// Check and request VPN permission
if (!ServiceManager.hasVpnPermission(this)) {
    ServiceManager.requestVpnPermission(this) { granted ->
        if (granted) {
            // VPN permission granted, ready to connect
        }
    }
}

// Check and request notification permission (Android 13+)
if (!ServiceManager.hasNotificationPermission(this)) {
    ServiceManager.requestNotificationPermission(this) { granted ->
        // Notification permission is optional but recommended
    }
}
```

### Java

```java
// Check and request VPN permission
if (!ServiceManager.INSTANCE.hasVpnPermission(this)) {
    ServiceManager.INSTANCE.requestVpnPermission(this, granted -> {
        if (granted) {
            // VPN permission granted, ready to connect
        }
    });
}

// Check and request notification permission (Android 13+)
if (!ServiceManager.INSTANCE.hasNotificationPermission(this)) {
    ServiceManager.INSTANCE.requestNotificationPermission(this, granted -> {
        // Notification permission is optional but recommended
    });
}
```

## Step 4: Connect to VPN

Create a configuration and start the VPN:

### Kotlin

```kotlin
import org.thebytearray.wireguard.model.TunnelConfig
import org.thebytearray.wireguard.service.ServiceManager

fun connectVpn() {
    val config = TunnelConfig.Builder()
        .setInterfaceAddress("10.0.0.2/24")
        .setPrivateKey("YOUR_PRIVATE_KEY_BASE64")
        .setListenPort(51820)
        .setPublicKey("PEER_PUBLIC_KEY_BASE64")
        .setAllowedIps(listOf("0.0.0.0/0"))
        .setEndpoint("vpn.example.com:51820")
        .build()

    ServiceManager.startVpnTunnel(this, config, null)
}
```

### Java

```java
import org.thebytearray.wireguard.model.TunnelConfig;
import org.thebytearray.wireguard.service.ServiceManager;
import java.util.Collections;

private void connectVpn() {
    TunnelConfig config = new TunnelConfig.Builder()
        .setInterfaceAddress("10.0.0.2/24")
        .setPrivateKey("YOUR_PRIVATE_KEY_BASE64")
        .setListenPort(51820)
        .setPublicKey("PEER_PUBLIC_KEY_BASE64")
        .setAllowedIps(Collections.singletonList("0.0.0.0/0"))
        .setEndpoint("vpn.example.com:51820")
        .build();

    ServiceManager.INSTANCE.startVpnTunnel(this, config, null);
}
```

## Step 5: Disconnect from VPN

### Kotlin

```kotlin
fun disconnectVpn() {
    ServiceManager.stopVpnTunnel(this)
}
```

### Java

```java
private void disconnectVpn() {
    ServiceManager.INSTANCE.stopVpnTunnel(this);
}
```

## Complete Example

Here's a complete minimal example:

### Kotlin

```kotlin
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize
        ServiceManager.setNotificationIcon(R.drawable.ic_vpn)
        
        // Setup buttons
        findViewById<Button>(R.id.btnConnect).setOnClickListener {
            checkPermissionsAndConnect()
        }
        
        findViewById<Button>(R.id.btnDisconnect).setOnClickListener {
            ServiceManager.stopVpnTunnel(this)
        }
    }
    
    private fun checkPermissionsAndConnect() {
        if (!ServiceManager.hasVpnPermission(this)) {
            ServiceManager.requestVpnPermission(this) { granted ->
                if (granted) connectVpn()
            }
        } else {
            connectVpn()
        }
    }
    
    private fun connectVpn() {
        val config = TunnelConfig.Builder()
            .setInterfaceAddress("10.0.0.2/24")
            .setPrivateKey("YOUR_PRIVATE_KEY")
            .setListenPort(51820)
            .setPublicKey("PEER_PUBLIC_KEY")
            .setAllowedIps(listOf("0.0.0.0/0"))
            .setEndpoint("vpn.example.com:51820")
            .build()
            
        ServiceManager.startVpnTunnel(this, config, null)
    }
}
```

---

**Next:** [API Reference](API-Reference.md) | [Configuration Details](Configuration.md)


