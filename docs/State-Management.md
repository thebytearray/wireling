# State management (stats broadcast)

While the tunnel runs, the library can emit stats on a **`Broadcast`** action defined in **`WireLingConstants`**.

## Constants (`org.thebytearray.wireling.sdk.WireLingConstants`)

| Constant | Role |
|----------|------|
| `STATS_BROADCAST_ACTION` | `IntentFilter` action |
| `EXTRA_STATE` | Connection state label (string) |
| `EXTRA_DURATION` | Uptime string |
| `EXTRA_DOWNLOAD_SPEED` | Download speed string |
| `EXTRA_UPLOAD_SPEED` | Upload speed string |
| `DEFAULT_*` | Fallback display values |

## Registering the receiver (recommended)

Use **`ContextCompat.registerReceiver`** so exported flags are set correctly on API 33+:

```kotlin
import androidx.core.content.ContextCompat
import org.thebytearray.wireling.sdk.WireLingConstants

val filter = IntentFilter(WireLingConstants.STATS_BROADCAST_ACTION)
ContextCompat.registerReceiver(
    context,
    receiver,
    filter,
    ContextCompat.RECEIVER_NOT_EXPORTED,
)
```

Unregister with **`context.unregisterReceiver(receiver)`** when the UI scope ends (e.g. `onStop`, or `DisposableEffect.onDispose` in Compose).

## Sample

See **`MainActivity`** in the **`app`** module: `WireLingSampleRoute` registers the receiver for the demo UI.

---

**Next:** [Troubleshooting](Troubleshooting.md)
