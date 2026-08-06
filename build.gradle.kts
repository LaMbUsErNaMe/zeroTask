plugins {
	kotlin("jvm") version "2.2.20"
	kotlin("plugin.spring") version "2.2.20"
	id("org.springframework.boot") version "3.5.7"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.openapi.generator") version "5.1.1"
	kotlin("plugin.jpa") version "2.2.20"
	kotlin("kapt") version "2.2.20"
    id("dev.detekt") version("2.0.0-alpha.1")

}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

detekt {
    buildUponDefaultConfig = true
    ignoreFailures = true
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

kapt {
	correctErrorTypes = true
}

dependencies {
	implementation ("org.springframework.boot:spring-boot-starter-webflux")
	implementation("io.github.oshai:kotlin-logging-jvm:8.0.01")

	implementation("org.springframework.kafka:spring-kafka")

	implementation("org.camunda.bpm:camunda-engine-plugin-spin:7.24.0")
	implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-rest:7.24.0")
	implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-webapp:7.24.0")
	implementation("org.camunda.spin:camunda-spin-dataformat-json-jackson:7.24.0")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14")

	implementation("org.liquibase:liquibase-core")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	implementation("com.h2database:h2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.mapstruct:mapstruct:1.6.2")
	kapt("org.mapstruct:mapstruct-processor:1.6.2")

}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
