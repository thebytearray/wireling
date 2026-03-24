# Installation

## JitPack (recommended)

**`settings.gradle.kts`**

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

**App `build.gradle.kts`**

```kotlin
dependencies {
    implementation("com.github.thebytearray:WireLing:<version>")
}
```

Use the [JitPack page](https://jitpack.io/#thebytearray/WireLing) for `<version>` (tag, branch, or commit).

## Local module

From a checkout of this repo:

```kotlin
// settings.gradle.kts
include(":app", ":wireling")

// app/build.gradle.kts
dependencies {
    implementation(project(":wireling"))
}
```

## Merged manifest (library)

The **`wireling`** AAR merges:

- Permissions: `INTERNET`, `ACCESS_NETWORK_STATE`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_SPECIAL_USE`, `POST_NOTIFICATIONS`
- Service: `org.thebytearray.wireling.sdk.platform.TunnelService` (not exported, `specialUse` + property `vpn`)

The **WireGuard Go backend** `VpnService` comes from the **wireguard-android** dependency manifest; do not duplicate it in your app.

## App responsibilities

Your application still needs:

1. **`VpnService` permission flow** — use `WireLingVpn` helpers and `VpnService.prepare`.
2. **Notification channel** — `WireLingVpn.createNotificationChannel(...)` in `Application.onCreate` (or before connect).
3. **Notification icon** — `WireLingVpn.setNotificationIcon(R.drawable....)` before `startVpnTunnel`.
4. **Optional**: `POST_NOTIFICATIONS` on API 33+ — `WireLingVpn.registerPostNotificationsLauncher` / `launchPostNotificationsRequest`.

---

**Next:** [Quick Start](Quick-Start.md)
