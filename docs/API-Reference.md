# API reference — `WireLingVpn`

Package: **`org.thebytearray.wireling`**

Object: **`WireLingVpn`** — static entry points for notifications, permissions, and tunnel control.

## Properties

| Name | Type | Description |
|------|------|-------------|
| `notificationIconResId` | `Int` | Last value passed to `setNotificationIcon` |
| `notificationChannelId` | `String` | Last channel id from `createNotificationChannel` |

## Notifications

### `createNotificationChannel(context, channelId, channelName, importance)`

Creates the channel (API 26+) and stores `channelId` for later use. Call from `Application.onCreate` or before starting the tunnel.

### `setNotificationIcon(resId: Int)`

Required before **`startVpnTunnel`**.

## Permissions

### `hasVpnPermission(context): Boolean`

`true` when `VpnService.prepare(context)` is `null`.

### `vpnPermissionPreparationIntent(context): Intent?`

Non-null when the user must approve VPN; launch with your **`ActivityResultLauncher`**.

### `registerVpnPermissionLauncher(activity, onResult): ActivityResultLauncher<Intent>`

### `launchVpnPermissionFlow(launcher, preparationIntent, onAlreadyGranted)`

### `hasNotificationPermission(context): Boolean`

### `registerPostNotificationsLauncher(activity, onResult): ActivityResultLauncher<String>`

### `launchPostNotificationsRequest(launcher)`

No-op below API 33 unless you guard externally.

## Tunnel

### `startVpnTunnel(context, config: TunnelConfig, blockedApps: List<String>?)`

Starts **`TunnelService`** as a foreground service. Requires icon + channel configured.

### `stopVpnTunnel(context)`

Sends stop to **`TunnelService`**.

---

**Related:** [WireLingConstants](State-Management.md) · [TunnelConfig](Configuration.md)
