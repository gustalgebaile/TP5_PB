plugins {
    `java-library`
    `maven-publish`
    jacoco
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    runtimeOnly(libs.org.springframework.boot.spring.boot.devtools)

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testImplementation("org.junit.platform:junit-platform-launcher:1.9.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.org.seleniumhq.selenium.selenium.java)
    testImplementation(libs.io.github.bonigarcia.webdrivermanager)
    testImplementation(libs.org.assertj.assertj.core)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.org.awaitility.awaitility)
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
version = "2.0-SNAPSHOT"
description = "TP3_PB"

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
