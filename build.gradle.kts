import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.7.21"
    application
}

group = "com.mckernant1.lol"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

application {
    mainClass.set("com.mckernant1.lol.esports.api.RunnerKt")
}

repositories {
    mavenCentral()
    maven(uri("https://mvn.mckernant1.com/release"))
}

configurations.implementation {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Utils
    implementation("com.github.mckernant1.lol:esports-api:0.0.22")
    implementation("com.github.mckernant1:kotlin-utils:0.0.34")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.18.25"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
