plugins {
    java
    id("org.liquibase.gradle") version "3.0.2"
}

buildscript {
    dependencies {
        classpath("org.liquibase:liquibase-core:4.33.0")
    }
}

dependencies {
    liquibaseRuntime("org.liquibase:liquibase-core:4.33.0")
    liquibaseRuntime("org.postgresql:postgresql:42.7.8")
}

liquibase {
    activities.register("main") {
        arguments = mapOf(
            "url" to "jdbc:postgresql://localhost:5432/road_freight_db",
            "username" to "appuser",
            "password" to "appsecret",
            "driver" to "org.postgresql.Driver",
            "changelogFile" to "src/main/resources/db/changelog-master.yml"
        )
    }
}