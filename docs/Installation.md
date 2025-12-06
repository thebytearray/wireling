# Installation

This guide covers all methods to integrate WGAndroidLib into your Android project.

## Option 1: JitPack (Recommended)

The easiest way to add WGAndroidLib to your project is through JitPack.

### Groovy DSL

Add JitPack repository to your root `build.gradle` or `settings.gradle`:

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Or in your root `build.gradle`:

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency to your app-level `build.gradle`:

```groovy
dependencies {
    implementation 'com.github.thebytearray:WGAndroidLib:<latest-version>'
}
```

Check the [JitPack badge](https://jitpack.io/#thebytearray/WGAndroidLib) or [releases page](https://github.com/thebytearray/WGAndroidLib/releases) for the latest version.

### Kotlin DSL

Add JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

Add the dependency to your app-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.thebytearray:WGAndroidLib:<latest-version>")
}
```

## Option 2: Git Submodule

For developers who want to customize the library or contribute to development.

### Step 1: Clone the Repository

```bash
git clone https://github.com/thebytearray/WGAndroidLib.git
```

### Step 2: Import as Module

In Android Studio:
1. Go to **File → New → Import Module**
2. Select the `wireguard` directory from the cloned repository
3. Follow the import wizard

### Step 3: Add Module Dependency

**Groovy DSL:**

```groovy
dependencies {
    implementation project(':wireguard')
}
```

**Kotlin DSL:**

```kotlin
dependencies {
    implementation(project(":wireguard"))
}
```

## Required Configuration

After adding the dependency, you must configure your `AndroidManifest.xml`.

### Permissions

Add the following permissions to your `AndroidManifest.xml`:

```xml
<!-- Required Permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />

<!-- For Android 13+ (API 33+) -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

### Service Declarations

Add these service declarations inside the `<application>` tag:

```xml
<!-- WireGuard Tunnel Service -->
<service
    android:name="org.thebytearray.wireguard.service.TunnelService"
    android:exported="true"
    android:foregroundServiceType="specialUse"
    android:permission="android.permission.FOREGROUND_SERVICE" />

<!-- VPN Backend Service -->
<service
    android:name="com.wireguard.android.backend.GoBackend$VpnService"
    android:exported="true"
    android:permission="android.permission.BIND_VPN_SERVICE">
    <intent-filter>
        <action android:name="android.net.VpnService" />
    </intent-filter>
</service>
```

## Sync and Build

After configuration, sync your Gradle files:

```bash
./gradlew sync
```

Or use **File → Sync Project with Gradle Files** in Android Studio.

---

**Next:** [Quick Start Guide](Quick-Start.md)

