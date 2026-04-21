import org.gradle.kotlin.dsl.coreLibraryDesugaring
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}

val versionProps = Properties().apply {
    val versionFile = rootProject.file("version.properties")
    if (versionFile.exists()) {
        load(versionFile.inputStream())
    }
}

val sdkVersion: String = versionProps.getProperty("VERSION_NAME", "1.0.0")
val mavenGroupId = "org.thebytearray.wireguard"
val mavenArtifactId = "WireLing"

val signingConfigured =
    !(project.findProperty("signingInMemoryKey") as String?).isNullOrBlank() ||
        !System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey").isNullOrBlank() ||
        (
            !(project.findProperty("signing.keyId") as String?).isNullOrBlank() &&
                !(project.findProperty("signing.secretKeyRingFile") as String?).isNullOrBlank()
            )

android {
    namespace = "org.thebytearray.wireling.sdk"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
        version = sdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
        explicitApi = ExplicitApiMode.Strict
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

mavenPublishing {
    coordinates(mavenGroupId, mavenArtifactId, sdkVersion)
    publishToMavenCentral(automaticRelease = true)
    if (signingConfigured) {
        signAllPublications()
    }

    pom {
        name.set("WireLing")
        description.set("Android WireGuard library with a simple API for VPN tunnels.")
        inceptionYear.set("2024")
        url.set("https://github.com/thebytearray/wireling")

        licenses {
            license {
                name.set("GNU General Public License v3.0")
                url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("thebytearray")
                name.set("TheByteArray")
                url.set("https://github.com/thebytearray/")
            }
        }

        scm {
            url.set("https://github.com/thebytearray/wireling/")
            connection.set("scm:git:git://github.com/thebytearray/wireling.git")
            developerConnection.set("scm:git:ssh://git@github.com/thebytearray/wireling.git")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.wireguard.tunnel)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
