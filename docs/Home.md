# WireLing documentation

WireLing is a simplified Android WireGuard integration. The public API is centered on **`org.thebytearray.wireling.WireLingVpn`** and **`org.thebytearray.wireling.domain.TunnelConfig`**.

Source: [thebytearray/wireling](https://github.com/thebytearray/wireling) on GitHub. Dependency: **`org.thebytearray.wireling:WireLing`** on Maven Central. Kotlin packages are under **`org.thebytearray.wireling`**.

## Features

- Start/stop VPN via a foreground **`TunnelService`**
- Notification channel + icon setup helpers
- VPN and POST_NOTIFICATIONS helpers using Activity Result APIs
- Stats **`BroadcastIntent`** (`WireLingConstants`) for UI updates
- **`TunnelConfig`** validation (IPv4 CIDR, Base64 keys, **IPv4:port** endpoint)

## Quick links

| Page | Description |
|------|-------------|
| [Installation](Installation.md) | Repositories, dependency, manifest |
| [Quick Start](Quick-Start.md) | Minimal integration steps |
| [API Reference](API-Reference.md) | `WireLingVpn` methods |
| [Configuration](Configuration.md) | Builder fields and limits |
| [State Management](State-Management.md) | Receiving stats in the UI |
| [Troubleshooting](Troubleshooting.md) | Typical failures |
| [Architecture](Architecture.md) | `wireling` module layers |

## Requirements

- **minSdk**: 24 (sample app); align with your product needs
- **Kotlin** / **Gradle**: see root `libs.versions.toml`
- **Maven Central**: `org.thebytearray.wireling:WireLing`

## License

GNU General Public License v3.0. See [LICENSE](../LICENSE).
