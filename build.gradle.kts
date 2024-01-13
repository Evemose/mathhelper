plugins {
    application
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}


dependencies {
    implementation(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    implementation(libs.org.springframework.boot.spring.boot.configuration.processor)
    implementation(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)
    implementation(libs.org.springframework.boot.spring.boot.starter.web)
    implementation(libs.org.springframework.boot.spring.boot.starter.validation)
    implementation(libs.org.springframework.boot.spring.boot.starter.security)
    implementation(libs.org.mapstruct.mapstruct)
    annotationProcessor(libs.org.mapstruct.mapstruct.processor)
    implementation(libs.org.projectlombok.lombok.mapstruct.binding)
    runtimeOnly(libs.org.springframework.boot.spring.boot.devtools)
    runtimeOnly(libs.org.postgresql.postgresql)
    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
    testImplementation(libs.com.h2database.h2)
    testImplementation(libs.org.springframework.security.spring.security.test)
    compileOnly(libs.org.mapstruct.mapstruct.processor)
}

configurations {

    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

group = "org"
version = "0.0.1-SNAPSHOT"
description = "mathhelper"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}


