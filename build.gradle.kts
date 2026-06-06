import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.2.2"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val mindustryVersion = "v158"
val jabelVersion = "93fde537c7"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
    maven { url = uri("https://www.jitpack.io") }
    ivy {
        url = uri("https://github.com/")
        patternLayout {
            artifact("/[organisation]/[module]/releases/download/[revision]/[artifact].jar")
        }
        metadataSources { artifact() }
    }
}

dependencies {
    compileOnly("Anuken:Mindustry:v158:dependencies")
    annotationProcessor("com.github.Anuken:jabel:$jabelVersion")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:slf4j-simple:2.0.9")

    implementation("org.codejargon.feather:feather:1.0")
    implementation("net.jodah:expiringmap:0.5.11")

    compileOnly("org.jetbrains:annotations:26.0.1")
}

val shadowJar = tasks.named<ShadowJar>("shadowJar")

tasks.register<JavaExec>("runServer") {
    description = "run test server"
    dependsOn(shadowJar)

    doFirst {
        copy {
            from(shadowJar.get().archiveFile)
            into(file("test/config/mods"))
        }
        println("Copied plugin jar to test/config/mods/")
    }

    workingDir = file("test")
    standardInput = System.`in`
    classpath = files("test/server.jar")
    mainClass.set("mindustry.server.ServerLauncher")
}
