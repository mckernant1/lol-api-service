import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    application
}

group = "com.mckernant1.lol.api"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

application {
    mainClass.set("com.mckernant1.lol.esports.api.RunnerKt")
}

repositories {
    mavenCentral()
    maven(uri("https://mvn.mckernant1.com/release"))
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")


	implementation("com.github.mckernant1.lol:esports-api:0.0.8")
	implementation("com.github.mckernant1:kotlin-utils:0.0.31")
	implementation("com.google.code.gson:gson:2.9.0")

	implementation("org.apache.logging.log4j:log4j-api:2.18.0")
	implementation("org.apache.logging.log4j:log4j-core:2.18.0")
	implementation("org.apache.logging.log4j:log4j-slf4j18-impl:2.18.0")

	implementation(platform("com.amazonaws:aws-java-sdk-bom:1.12.290"))
	implementation("com.amazonaws:aws-java-sdk-dynamodb")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
