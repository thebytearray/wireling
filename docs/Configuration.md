# Configuration Guide

Detailed guide on WireGuard configuration and validation in WGAndroidLib.

## TunnelConfig Structure

A `TunnelConfig` consists of two main parts:

```
TunnelConfig
├── Interface
│   ├── address (String)      - Your device's VPN IP address
│   ├── privateKey (String)   - Your device's private key
│   └── listenPort (Long)     - UDP port for WireGuard
│
└── Peer
    ├── publicKey (String)    - VPN server's public key
    ├── allowedIps (List)     - IP ranges to route through VPN
    └── endpoint (String)     - VPN server address and port
```

## Configuration Parameters

### Interface Configuration

#### Address

The IP address assigned to your device on the VPN network.

```kotlin
.setInterfaceAddress("10.0.0.2/24")
```

- **Format**: IPv4 address with CIDR notation
- **Example**: `10.0.0.2/24`, `192.168.1.100/32`
- **CIDR**: Defines the subnet mask (`/24` = 255.255.255.0)

#### Private Key

Your device's WireGuard private key (Base64 encoded).

```kotlin
.setPrivateKey("YHR3e4a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8=")
```

- **Format**: 44-character Base64 string ending with `=`
- **Generation**: Use `wg genkey` command or WireGuard tools
- **Security**: Never share this key!

#### Listen Port

The UDP port WireGuard uses for communication.

```kotlin
.setListenPort(51820)
```

- **Range**: 1 to 65535
- **Default**: Usually `51820`
- **Note**: Can be any valid port; server must be configured accordingly

### Peer Configuration

#### Public Key

The VPN server's public key (Base64 encoded).

```kotlin
.setPublicKey("SERVER_PUBLIC_KEY_BASE64")
```

- **Format**: 44-character Base64 string ending with `=`
- **Source**: Provided by your VPN server administrator

#### Allowed IPs

IP ranges that should be routed through the VPN tunnel.

```kotlin
.setAllowedIps(listOf("0.0.0.0/0"))
```

**Common Configurations:**

| Configuration | Description |
|---------------|-------------|
| `0.0.0.0/0` | Route all IPv4 traffic through VPN (full tunnel) |
| `0.0.0.0/0, ::/0` | Route all IPv4 and IPv6 traffic |
| `10.0.0.0/8` | Only route traffic to 10.x.x.x addresses |
| `192.168.1.0/24` | Only route traffic to 192.168.1.x addresses |

**Split Tunneling Example:**

```kotlin
.setAllowedIps(listOf(
    "10.0.0.0/8",        // Private network
    "172.16.0.0/12",     // Private network
    "192.168.0.0/16"     // Private network
))
```

#### Endpoint

The VPN server's address and port.

```kotlin
.setEndpoint("vpn.example.com:51820")
```

- **Format**: `IP:Port` or `hostname:Port`
- **Examples**: `192.168.1.1:51820`, `vpn.example.com:51820`

## Complete Configuration Examples

### Full Tunnel (All Traffic)

Routes all device traffic through the VPN:

```kotlin
val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")
    .setPrivateKey("YOUR_PRIVATE_KEY")
    .setListenPort(51820)
    .setPublicKey("SERVER_PUBLIC_KEY")
    .setAllowedIps(listOf("0.0.0.0/0"))
    .setEndpoint("vpn.example.com:51820")
    .build()
```

### Split Tunnel (Specific Networks Only)

Routes only specific network traffic through VPN:

```kotlin
val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")
    .setPrivateKey("YOUR_PRIVATE_KEY")
    .setListenPort(51820)
    .setPublicKey("SERVER_PUBLIC_KEY")
    .setAllowedIps(listOf(
        "10.0.0.0/8",
        "192.168.0.0/16"
    ))
    .setEndpoint("vpn.example.com:51820")
    .build()
```

### With App Exclusions

Exclude specific apps from the VPN:

```kotlin
val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")
    .setPrivateKey("YOUR_PRIVATE_KEY")
    .setListenPort(51820)
    .setPublicKey("SERVER_PUBLIC_KEY")
    .setAllowedIps(listOf("0.0.0.0/0"))
    .setEndpoint("vpn.example.com:51820")
    .build()

// Apps that bypass VPN
val excludedApps = listOf(
    "com.example.banking",
    "com.example.streaming"
)

ServiceManager.startVpnTunnel(context, config, excludedApps)
```

## Validation

WGAndroidLib automatically validates all configuration parameters when building:

### Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| Interface Address | Must be valid IPv4 with optional CIDR | "Invalid interface address format" |
| Private Key | Must be valid Base64 WireGuard key | "Invalid private key format" |
| Listen Port | Must be between 1 and 65535 | "Invalid listen port" |
| Public Key | Must be valid Base64 WireGuard key | "Invalid public key format" |
| Allowed IPs | All entries must be valid IPv4/CIDR | "Invalid allowed IPs format" |
| Endpoint | Must be valid IP:Port format | "Invalid endpoint format" |

### Handling Validation Errors

```kotlin
try {
    val config = TunnelConfig.Builder()
        .setInterfaceAddress("invalid-address")  // This will fail
        .setPrivateKey("YOUR_PRIVATE_KEY")
        .setListenPort(51820)
        .setPublicKey("SERVER_PUBLIC_KEY")
        .setAllowedIps(listOf("0.0.0.0/0"))
        .setEndpoint("vpn.example.com:51820")
        .build()
} catch (e: IllegalArgumentException) {
    // Handle validation error
    Log.e("Config", "Invalid configuration: ${e.message}")
}
```

### Required Fields Check

The builder also checks for required fields:

```kotlin
try {
    val config = TunnelConfig.Builder()
        .setInterfaceAddress("10.0.0.2/24")
        // Missing privateKey, publicKey, endpoint, allowedIps
        .build()
} catch (e: IllegalArgumentException) {
    // "Private key is required"
    // "Public key is required"
    // "Endpoint is required"
    // "At least one allowed IP is required"
}
```

## WireGuard Key Generation

### Using Command Line

```bash
# Generate private key
wg genkey > private.key

# Generate public key from private key
cat private.key | wg pubkey > public.key
```

### Using WireGuard Apps

Most WireGuard apps (including the official app) can generate key pairs for you.

### Key Format

- **Length**: 44 characters (Base64)
- **Ending**: Always ends with `=`
- **Example**: `YHR3e4a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8=`

## Converting from .conf Files

If you have a WireGuard `.conf` file:

```ini
[Interface]
PrivateKey = YHR3e4a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8=
Address = 10.0.0.2/24
ListenPort = 51820

[Peer]
PublicKey = SERVER_PUBLIC_KEY_BASE64
AllowedIPs = 0.0.0.0/0
Endpoint = vpn.example.com:51820
```

Map it to TunnelConfig:

```kotlin
val config = TunnelConfig.Builder()
    .setInterfaceAddress("10.0.0.2/24")           // [Interface] Address
    .setPrivateKey("YHR3e4a1...r8=")              // [Interface] PrivateKey
    .setListenPort(51820)                          // [Interface] ListenPort
    .setPublicKey("SERVER_PUBLIC_KEY_BASE64")     // [Peer] PublicKey
    .setAllowedIps(listOf("0.0.0.0/0"))           // [Peer] AllowedIPs
    .setEndpoint("vpn.example.com:51820")         // [Peer] Endpoint
    .build()
```

---

**Next:** [State Management](State-Management.md) | [Troubleshooting](Troubleshooting.md)


