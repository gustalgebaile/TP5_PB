plugins {
    `java-library`
    `maven-publish`
    jacoco
    id("com.gradleup.shadow") version "9.2.2"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web:3.1.4")
    api("org.springframework.boot:spring-boot-starter-validation:3.1.4")
    runtimeOnly("org.springframework.boot:spring-boot-devtools:3.1.4")
    runtimeOnly("io.javalin:javalin-rendering:6.7.0")

    implementation("io.javalin:javalin:6.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("org.junit.platform:junit-platform-launcher:1.9.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.11.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.5.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.4")
    testImplementation("org.awaitility:awaitility:4.2.0")
}
sourceSets {
    main {
        java.srcDirs("src/main/java")
    }
    test {
        java.srcDirs("src/test/java")
    }
}

group = "com.biblioteca"
version = "1.0-SNAPSHOT"
description = "TP5_PB"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

jacoco {
    toolVersion = "0.8.14"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    manifest {
        attributes["Main-Class"] = "com.biblioteca.BibliotecaWebApplication"
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "jdk/**", "java/**", "javax/**", "sun/**",
                    "org/springframework/**", "org/junit/**"
                )
            }
        })
    )
}
