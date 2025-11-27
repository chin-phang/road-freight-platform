plugins {
    `maven-publish`
    `java-library`
}

version = "0.0.1-SNAPSHOT"
description = "Shared Common for Road Freight Platform"

dependencies {
    api("jakarta.persistence:jakarta.persistence-api")
    api("jakarta.validation:jakarta.validation-api")
}