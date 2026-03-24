# Troubleshooting

## "Notification icon must be set before starting VPN"

Call **`WireLingVpn.setNotificationIcon(R.drawable....)`** before **`startVpnTunnel`**.

## "Notification channel must be created..."

Call **`WireLingVpn.createNotificationChannel(...)`** first (typically in `Application.onCreate`).

## Invalid interface address / keys / endpoint

- Address: IPv4 + CIDR (e.g. `10.0.0.2/24`).
- Keys: 44-char Base64 ending with `=`.
- Endpoint: **`IPv4:port`** only in current validation — not `hostname:port`.

## "Invalid listen port"

Use **1–65535**. Pass as **`Long`** to the builder.

## VPN permission denied

Ensure you run **`VpnService.prepare`** flow and only call **`startVpnTunnel`** after the user grants access.

## Merge / manifest conflicts for `GoBackend$VpnService`

Declare the WireGuard VPN service **once**. Rely on the **wireguard-android** AAR manifest; avoid duplicating `com.wireguard.android.backend.GoBackend$VpnService` in your library manifest.

---

**Back:** [Home](Home.md)
