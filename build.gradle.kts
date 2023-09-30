import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.20"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "ventre"
version = "1.0-SNAPSHOT"
allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}

application {
    mainClassName = "ventre.qqrobot.LoginKt"
}

dependencies {
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation ("net.mamoe:mirai-core:2.15.0")
    implementation ("com.alibaba:fastjson:1.2.70")
    implementation("commons-io:commons-io:2.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.3")
    implementation("io.lettuce:lettuce-core:6.0.1.RELEASE")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"
    }

    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("ventre_qq_robot")
        mergeServiceFiles()
    }
}