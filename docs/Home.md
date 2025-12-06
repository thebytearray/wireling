# WGAndroidLib Documentation

Welcome to the official documentation for **WGAndroidLib** — a simplified Android WireGuard library that provides easy-to-use APIs for implementing VPN functionality in your Android applications.

## Overview

WGAndroidLib is a wrapper around the official [wireguard-android](https://github.com/WireGuard/wireguard-android) project, designed to simplify VPN implementation without dealing with complex low-level functions.

## Features

- **Developer-Friendly APIs** — Simple methods to start, stop, and monitor VPN connections
- **State Management** — Built-in broadcast receiver for real-time connection state updates
- **Notification System** — Automatic foreground service notifications with speed statistics
- **Configuration Validation** — Built-in validation for WireGuard configurations
- **Permission Handling** — Easy-to-use permission request APIs for VPN and notifications

## Quick Links

| Documentation | Description |
|---------------|-------------|
| [Installation](Installation.md) | How to add the library to your project |
| [Quick Start](Quick-Start.md) | Get up and running in minutes |
| [API Reference](API-Reference.md) | Complete API documentation |
| [Configuration](Configuration.md) | TunnelConfig and validation details |
| [State Management](State-Management.md) | Handling VPN states and broadcasts |
| [Troubleshooting](Troubleshooting.md) | Common issues and solutions |

## Requirements

- **Minimum SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 34 (Android 14)
- **Kotlin**: 1.9+
- **Gradle**: 8.0+

## License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**.

## Trademark Notice

"WireGuard" and the "WireGuard" logo are registered trademarks of Jason A. Donenfeld.

---

*Maintained by [TheByteArray](https://thebytearray.org) • [GitHub Repository](https://github.com/thebytearray/WGAndroidLib)*

