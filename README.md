# WireLing

[![CI](https://github.com/thebytearray/wireling/actions/workflows/ci.yml/badge.svg)](https://github.com/thebytearray/wireling/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.thebytearray.wireguard/WireLing)](https://central.sonatype.com/artifact/org.thebytearray.wireguard/WireLing)
[![GitHub release](https://img.shields.io/github/v/release/thebytearray/wireling)](https://github.com/thebytearray/wireling/releases)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

WireLing is an Android library that wraps [wireguard-android](https://github.com/WireGuard/wireguard-android) with a small public API (`WireLingVpn`, `TunnelConfig`, foreground notifications, and stats broadcasts). Kotlin sources live under **`org.thebytearray.wireling`**. This project continues **WGAndroidLib** under the WireLing name and coordinates.

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

## Quick install (Maven Central)

Dependency: **`org.thebytearray.wireguard:WireLing`**. Use `google()` and `mavenCentral()`. Versions: [Maven Central](https://central.sonatype.com/artifact/org.thebytearray.wireguard/WireLing) and [GitHub Releases](https://github.com/thebytearray/wireling/releases).

**Kotlin DSL:**

```kotlin
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.thebytearray.wireguard:WireLing:<version>")
}
```

**Groovy:**

```groovy
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation 'org.thebytearray.wireguard:WireLing:<version>'
}
```

## Build locally

```bash
./gradlew :wireling:assembleRelease :app:assembleDebug
./gradlew :wireling:testDebugUnitTest
```

## Namespaces

| Module | Android `namespace` | Kotlin packages |
|--------|---------------------|-----------------|
| `wireling` | `org.thebytearray.wireling` | `org.thebytearray.wireling` (and internal subpackages) |
| `app` | `org.thebytearray.wireling` | Sample: `org.thebytearray.wireling.sample` |

## License

WireLing is licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).

“WireGuard” and the WireGuard logo are registered trademarks of Jason A. Donenfeld.
