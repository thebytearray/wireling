# WireLing

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![](https://jitpack.io/v/thebytearray/WireLing.svg)](https://jitpack.io/#thebytearray/WireLing)

WireLing is an Android library that wraps [wireguard-android](https://github.com/WireGuard/wireguard-android) with a small public API (`WireLingVpn`, `TunnelConfig`, foreground notifications, and stats broadcasts). Kotlin sources for the library live under **`org.thebytearray.wireling.sdk`**.

The **`app`** module is a sample you can run to enter a WireGuard-style tunnel (interface, keys, peer, endpoint) and connect.

## Documentation

| Guide | Description |
|-------|-------------|
| [docs/Home.md](docs/Home.md) | Overview and index |
| [docs/Installation.md](docs/Installation.md) | Gradle / manifest setup |
| [docs/Quick-Start.md](docs/Quick-Start.md) | Channel, icon, permissions, connect |
| [docs/API-Reference.md](docs/API-Reference.md) | `WireLingVpn` entry points |
| [docs/Configuration.md](docs/Configuration.md) | `TunnelConfig` and validation |
| [docs/State-Management.md](docs/State-Management.md) | Stats broadcast and UI |
| [docs/Troubleshooting.md](docs/Troubleshooting.md) | Common errors |
| [docs/Architecture.md](docs/Architecture.md) | Module and layer layout |

## Quick install (JitPack)

In `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

In your app `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.thebytearray:WireLing:<tag-or-commit>")
}
```

Replace `<tag-or-commit>` with a [JitPack](https://jitpack.io/#thebytearray/WireLing) version.

## Build locally

```bash
./gradlew :wireling:assembleRelease :app:assembleDebug
./gradlew :wireling:testDebugUnitTest
```

## Namespaces

| Module | Android `namespace` | Kotlin packages |
|--------|---------------------|-----------------|
| `wireling` | `org.thebytearray.wireling.library` | `org.thebytearray.wireling.sdk` (and internal subpackages) |
| `app` | `org.thebytearray.wireling.app` | Sample: `org.thebytearray.wireling.sample` |

## License

WireLing is licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).

“WireGuard” and the WireGuard logo are registered trademarks of Jason A. Donenfeld.

## Git history note

If this tree was checked out with history carried over from **WGAndroidLib**, your working copy may not match the last commit until you record the new layout (e.g. `git add -A` and a merge or snapshot commit). That preserves prior commits without rebasing the same patches by hand.
