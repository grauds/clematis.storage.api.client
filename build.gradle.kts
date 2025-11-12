import org.gradle.kotlin.dsl.annotationProcessor
import java.net.URI

plugins {
    id("org.springframework.boot") version "3.3.5" apply false
    id("io.spring.dependency-management") version "1.1.6"
    id("java-library")
    id("checkstyle")
    id("maven-publish")
}

// Apply Spring Boot dependency management without applying the full Spring Boot plugin
// This ensures we get all the version management without the application packaging features
apply(plugin = "io.spring.dependency-management")

group = "org.clematis"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

// Enable compiler parameters to retain parameter names
tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
}

// Configure the dependency management to use Spring Boot's BOM
the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.3")
    }
}

dependencies {
    // ---- Feign client core ----
    api("org.springframework.cloud:spring-cloud-starter-openfeign")

// ---- Optional: Lombok ----
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    checkstyle("com.puppycrawl.tools:checkstyle:10.9.3")

    implementation("com.fasterxml.jackson.core:jackson-databind")

    // ---- Testing ----
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

checkstyle {
    configProperties["configFile"] = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
    configProperties["checkstyleSuppressionFile"] = file("${project.rootDir}/config/checkstyle/suppressions.xml")
}


publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI.create("https://maven.pkg.github.com/grauds/clematis.storage.api.client")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "storage-api-client"
        }
    }
}
