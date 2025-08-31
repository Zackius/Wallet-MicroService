plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Wallet App Project"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}
configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
	mavenCentral()
}
val mapstructVersion = "1.5.5.Final"
val tsidCreatorVersion = "5.2.0"
val commonsLangVersion = "3.12.0"
val springdocVersion = "2.7.0"
val lettuceVersion = "6.5.2.RELEASE"
val okHttpVersion = "5.0.0-alpha.14"
val hibernateVersion = "6.0.6.Final"
val tsidVersion = "2.1.4"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation ("org.springframework.boot:spring-boot-starter-security")

    implementation("org.postgresql:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")


    implementation("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    implementation("io.lettuce:lettuce-core:${lettuceVersion}")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.9.0")

    implementation("org.mapstruct:mapstruct:${mapstructVersion}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${mapstructVersion}")

    implementation("org.apache.commons:commons-lang3:${commonsLangVersion}")
    implementation("com.github.f4b6a3:tsid-creator:${tsidCreatorVersion}")
    implementation("com.google.code.gson:gson:2.11.0")
   // implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-core:10.17.2")
    implementation("org.flywaydb:flyway-database-postgresql:10.17.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springdocVersion}")

    implementation("io.hypersistence:hypersistence-tsid:${tsidVersion}")

    implementation("org.hibernate.common:hibernate-commons-annotations:${hibernateVersion}")

    implementation("com.fasterxml.jackson.core:jackson-databind")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")



}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.isFork = true
    options.forkOptions.apply {
        memoryInitialSize = "512m"
        memoryMaximumSize = "2g"
        jvmArgs = listOf("-Xmx2G", "-XX:+UseParallelGC")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    maxParallelForks = Runtime.getRuntime().availableProcessors().div(2)
}

tasks.withType<Wrapper> {
    gradleVersion = "8.4.1"
    distributionType = Wrapper.DistributionType.ALL
}

