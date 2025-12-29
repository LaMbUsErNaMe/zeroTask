plugins {
	kotlin("jvm") version "2.2.20"
	kotlin("plugin.spring") version "2.2.20"
	id("org.springframework.boot") version "4.0.0"
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
    //config.setFrom(files("$rootDir/detekt.yml"))
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
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")
	implementation("javax.validation:validation-api:2.0.0.Final")
	implementation("org.springframework.boot:spring-boot-starter-liquibase")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("org.postgresql:postgresql")
    testImplementation("com.ninja-squad:springmockk:5.0.1")
	testImplementation("org.springframework.boot:spring-boot-starter-liquibase-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
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
