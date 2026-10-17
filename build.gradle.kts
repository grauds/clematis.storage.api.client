import org.gradle.kotlin.dsl.annotationProcessor
import java.net.URI

plugins {
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("java-library")
    id("checkstyle")
    id("maven-publish")
}

group = "org.clematis"
version = "1.1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
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

dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2026.0.0-M1")
    }
}

dependencies {
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-http-converter")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    checkstyle("com.puppycrawl.tools:checkstyle:10.9.3")

    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

checkstyle {
    configProperties["configFile"] = file("$rootDir/config/checkstyle/checkstyle.xml")
    configProperties["checkstyleSuppressionFile"] = file("$rootDir/config/checkstyle/suppressions.xml")
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
            groupId = "${project.group}.storage"
            artifactId = "storage-api-client"
            version = "${project.version}"
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}
