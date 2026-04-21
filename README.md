# WireLing

[![CI](https://github.com/thebytearray/wireling/actions/workflows/ci.yml/badge.svg)](https://github.com/thebytearray/wireling/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.thebytearray.wireling/WireLing)](https://central.sonatype.com/artifact/org.thebytearray.wireling/WireLing)
[![GitHub release](https://img.shields.io/github/v/release/thebytearray/wireling)](https://github.com/thebytearray/wireling/releases)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

WireLing is an Android library for WireGuard-style VPN tunnels with a small public API: `WireLingVpn`, `TunnelConfig`, foreground notifications, and stats broadcasts. Kotlin sources and the Android `namespace` use **`org.thebytearray.wireling`**. Maven Central artifact: **`org.thebytearray.wireling:WireLing`**.

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

Dependency: **`org.thebytearray.wireling:WireLing`**. Use `google()` and `mavenCentral()`. Versions: [Maven Central](https://central.sonatype.com/artifact/org.thebytearray.wireling/WireLing) and [GitHub Releases](https://github.com/thebytearray/wireling/releases).

**Kotlin DSL:**

```kotlin
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.thebytearray.wireling:WireLing:<version>")
}
```

**Groovy:**

```groovy
repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation 'org.thebytearray.wireling:WireLing:<version>'
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
| `app` | `org.thebytearray.wireling.sample` | Sample: `org.thebytearray.wireling.sample` (and `org.thebytearray.wireling.ui.theme`) |

## License

WireLing is licensed under the **GNU General Public License v3.0**. See [LICENSE](LICENSE).

“WireGuard” and the WireGuard logo are registered trademarks of Jason A. Donenfeld.
