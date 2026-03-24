# Configuration (`TunnelConfig`)

`TunnelConfig` is immutable and built with **`TunnelConfig.Builder`**. Validation runs in `init` when you call **`build()`**.

## Structure

```
TunnelConfig
├── Interface — address (CIDR), privateKey (Base64), listenPort (Long)
└── Peer — publicKey (Base64), allowedIps (List), endpoint (String)
```

## Interface address

IPv4 with CIDR, e.g. `10.0.0.2/24`, `192.168.1.5/32`.

## Keys

Private and peer public keys must match:

- 44 characters
- Base64 alphabet
- Trailing `=`

## Listen port

`1`–`65535` as **`Long`** in the builder (`setListenPort(51820L)`).

## Allowed IPs

Non-empty list of IPv4 CIDR strings, e.g. `listOf("0.0.0.0/0")` for full IPv4 tunnel.

## Endpoint

**Important:** validation currently accepts **`IPv4:port` only** (e.g. `198.51.100.10:51820`). Hostnames are **not** accepted by the regex; resolve DNS in your app first if you need a hostname.

## Blocked / excluded packages

Pass a list of package names as the third argument to **`WireLingVpn.startVpnTunnel`**, or `null` for none. Semantics follow the underlying tunnel implementation.

---

**Next:** [Troubleshooting](Troubleshooting.md)
