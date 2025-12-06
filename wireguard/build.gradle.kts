import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.maven)
}

val versionProps = Properties().apply {
    val versionFile = rootProject.file("version.properties")
    if (versionFile.exists()) {
        load(versionFile.inputStream())
    }
}

val sdkVersion: String = versionProps.getProperty("VERSION_NAME", "1.0.0")
val sdkVersionCode: Int = versionProps.getProperty("VERSION_CODE", "1").toInt()
val jitpackGroupId = "com.github.thebytearray"
val packageId = "org.thebytearray.wireguard"

android {
    namespace = packageId
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        version = sdkVersion

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

publishing {
    publications {
        create("release", MavenPublication::class) {
            groupId = jitpackGroupId
            artifactId = "WGAndroidLib"
            version = sdkVersion

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.coroutine)
    implementation(libs.tunnel)
    implementation(libs.gson)
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
}
