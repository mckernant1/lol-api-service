import com.google.protobuf.gradle.id

plugins {
    id("org.springframework.boot") version "4.0.1"
    kotlin("jvm") version "2.2.21"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.spring") version "2.2.21"
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
    maven(uri("https://repo.spring.io/milestone"))
    maven(uri("https://repo.spring.io/snapshot"))
}

configurations.implementation {
    exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude(group = "ch.qos.logback")
}

val gRpcVersion = "1.76.0"
val googleProtobufVersion = "4.33.0"
val kotlinStubVersion = "1.4.3"

dependencyManagement {
    imports {
        mavenBom("org.springframework.grpc:spring-grpc-dependencies:1.0.0")
    }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.grpc:spring-grpc-server-spring-boot-starter")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Utils
    implementation("com.mckernant1.lol:esports-api:0.2.0")
    implementation("com.mckernant1:kotlin-utils:0.3.0")
    implementation("com.mckernant1.commons:metrics:0.0.10")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.1")
    implementation("com.google.guava:guava:33.5.0-jre")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.25.3")

    // AWS
    implementation(platform("software.amazon.awssdk:bom:2.40.16"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:cloudwatch")

    // Protobuf

    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-kotlin-stub:${kotlinStubVersion}")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-services")
    protobuf("com.google.protobuf:protobuf-java")
    implementation("com.google.protobuf:protobuf-kotlin")
    implementation("com.google.protobuf:protobuf-java-util")

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
        artifact = "com.google.protobuf:protoc"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:$kotlinStubVersion:jdk8@jar"
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
