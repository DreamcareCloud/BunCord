import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    id("org.springframework.boot") version "3.0.5"
    id("com.palantir.git-version") version "3.0.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

apply(plugin = "io.spring.dependency-management")

group = "cloud.dreamcare"
version = "1.0.0"

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()


java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Discord
    implementation("net.dv8tion:JDA:5.0.0-beta.6")

    // Developer Tools
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
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
