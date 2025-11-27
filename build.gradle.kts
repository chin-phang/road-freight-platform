import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("org.springframework.boot") version "3.5.8"
}

allprojects {
    group = "com.minelog"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        implementation(platform(SpringBootPlugin.BOM_COORDINATES))
        implementation("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
    }
}