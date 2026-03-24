# Architecture

## Gradle modules

| Module | Role |
|--------|------|
| **`wireling`** | Published Android library (AAR). |
| **`app`** | Sample application demonstrating `WireLingVpn` and editable `TunnelConfig`. |

## `wireling` layers (Kotlin)

Under **`org.thebytearray.wireling.sdk`**:

- **`domain`** — `TunnelConfig`, `TunnelState`, `VpnTunnelEngine` (interfaces and models).
- **`data`** — WireGuard engine adapter, persistence helpers, tunnel wiring.
- **`platform`** — Android `TunnelService`, intents, notifications, Android-specific glue.
- **Root `sdk` package** — **`WireLingVpn`**, **`WireLingConstants`** (public façade).

Internal types stay in subpackages; consumers should depend on the public API above.

## Android namespaces

AGP requires a unique **`namespace`** per module. This project uses:

- **`org.thebytearray.wireling.library`** for the **`wireling`** module.
- **`org.thebytearray.wireling.app`** for the **`app`** module.

Kotlin package names (**`org.thebytearray.wireling.sdk`**, **`org.thebytearray.wireling.sample`**) are independent of the manifest namespace string.

## Dependencies

- **wireguard-android** tunnel backend (see `wireling/build.gradle.kts`).
- AndroidX (core, activity, appcompat as needed), Kotlin coroutines.

---

**Back:** [Home](Home.md)
