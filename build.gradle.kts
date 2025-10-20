plugins {
    kotlin("jvm") version "2.2.0"
    application
}

group = "ru.mirea"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin стандартная библиотека
    implementation(kotlin("stdlib"))

    // Веб-фреймворк Ktor (альтернатива Spring)
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-server-html-builder:2.3.5")

    // База данных
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")

    // Логирование
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Тестирование
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host:2.3.5")
}

application {
    mainClass.set("ru.mirea.education.MainKt")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "22"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "22"
    }

    // Создание fat JAR с зависимостями
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("education-app")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

kotlin {
    jvmToolchain(22)
}