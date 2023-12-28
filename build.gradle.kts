import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
}

group = "com.llamalad7"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.jkcclemens:khttp:0.1.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.0")
    implementation("com.github.aballano:mnemonik:2.1.1")
    implementation(project.files("z3/com.microsoft.z3.jar"))
    implementation("guru.nidi:graphviz-kotlin:0.18.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    compilerOptions.freeCompilerArgs.add("-Xcontext-receivers")
}