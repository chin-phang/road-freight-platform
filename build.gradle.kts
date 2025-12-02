plugins {
    java
}

allprojects {
    group = "com.minelog"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        implementation("org.projectlombok:lombok:1.18.42")
        annotationProcessor("org.projectlombok:lombok:1.18.42")
    }
}