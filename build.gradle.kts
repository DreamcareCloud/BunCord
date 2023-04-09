plugins {
    application
    kotlin("jvm") version "1.8.20"
    id("com.palantir.git-version") version "3.0.0"
}

application {
    mainClass.set("cloud.dreamcare.buncord.BunCordKt")
}

kotlin {
    jvmToolchain(17)
    explicitApi()
}

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

repositories {
    mavenCentral()
}

dependencies {
    // Discord
    implementation("dev.kord:kord-core:0.8.3")

    // Logging
    implementation("io.github.oshai", "kotlin-logging-jvm", "4.0.0-beta-28")
    implementation("ch.qos.logback", "logback-classic", "1.4.6")

    // Tools
    implementation("io.github.cdimascio", "dotenv-kotlin", "6.4.1")
    implementation("org.reflections", "reflections", "0.10.2")
}

tasks {
    val fatJar = register<Jar>("fatJar") {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("standalone")

        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        from(sourceSets.main.get().output)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
    build {
        dependsOn(fatJar)
    }
}
