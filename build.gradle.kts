import com.google.protobuf.gradle.id

plugins {
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.6"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "2.1.21"
    id("com.google.protobuf") version "0.9.5"
    application
}

group = "com.mckernant1.lol"
version = "0.0.1-SNAPSHOT"
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass.set("com.mckernant1.lol.esports.api.RunnerKt")
}

repositories {
    mavenCentral()
    maven(uri("https://mvn.mckernant1.com/release"))
}

configurations.implementation {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude(group = "ch.qos.logback")
}

val gRpcVersion = "1.76.0"
val googleProtobufVersion = "4.28.2"

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Utils
    implementation("com.mckernant1.lol:esports-api:0.2.0")
    implementation("com.mckernant1:kotlin-utils:0.3.0")
    implementation("com.mckernant1.commons:metrics:0.0.10")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("com.google.guava:guava:33.5.0-jre")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.25.2")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.37.1"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:cloudwatch")

    // Protobuf

    implementation("io.grpc:grpc-protobuf:${gRpcVersion}")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-stub:${gRpcVersion}")
    implementation("io.grpc:grpc-services:${gRpcVersion}")
    protobuf("com.google.protobuf:protobuf-java:${googleProtobufVersion}")
    implementation("com.google.protobuf:protobuf-kotlin:${googleProtobufVersion}")
    implementation("com.google.protobuf:protobuf-java-util:${googleProtobufVersion}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

sourceSets {
    main {
        proto {
            srcDir("lol-grpc-models/proto")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${googleProtobufVersion}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$gRpcVersion"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }

    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    outputSubDir = "java"
                }
                id("grpckt") {
                    outputSubDir = "kotlin"
                }
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
