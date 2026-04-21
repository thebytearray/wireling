# Architecture

## Gradle modules

| Module | Role |
|--------|------|
| **`wireling`** | Published Android library (AAR). |
| **`app`** | Sample application demonstrating `WireLingVpn` and editable `TunnelConfig`. |

## `wireling` layers (Kotlin)

Under **`org.thebytearray.wireling`**:

- **`domain`** — `TunnelConfig`, `TunnelState`, `VpnTunnelEngine` (interfaces and models).
- **`data`** — WireGuard engine adapter, persistence helpers, tunnel wiring.
- **`platform`** — Android `TunnelService`, intents, notifications, Android-specific glue.
- **Library root** — **`WireLingVpn`**, **`WireLingConstants`** (public façade).

Internal types stay in subpackages; consumers should depend on the public API above.

## Android namespaces

AGP requires a unique **`namespace`** per module. This project uses:

- **`org.thebytearray.wireling`** for the **`wireling`** module.
- **`org.thebytearray.wireling.sample`** for the **`app`** module.

The library **`namespace`** and Kotlin packages share the root **`org.thebytearray.wireling`** (the sample app also uses **`org.thebytearray.wireling.sample`** and **`org.thebytearray.wireling.ui.theme`**).

## Dependencies

- **wireguard-android** tunnel backend (see `wireling/build.gradle.kts`).
- AndroidX (core, activity, appcompat as needed), Kotlin coroutines.

---

**Back:** [Home](Home.md)
