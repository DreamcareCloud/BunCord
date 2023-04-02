plugins {
    application
    id("org.springframework.boot") version "3.0.5"
    id("com.palantir.git-version") version "3.0.0"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
}
apply(plugin = "io.spring.dependency-management")

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Kotlin
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Developer Tools
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

    // Discord
    implementation("net.dv8tion:JDA:5.0.0-beta.6")
    implementation("com.github.minndevelopment:jda-reactor:1.6.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
