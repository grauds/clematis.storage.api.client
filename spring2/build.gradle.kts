import java.net.URI

plugins {
    id("io.spring.dependency-management") version "1.1.7"
    id("java-library")
    id("checkstyle")
    id("maven-publish")
}

group = rootProject.group
version = rootProject.version

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

// Reuse source from the root project — no duplication
sourceSets {
    main {
        java { setSrcDirs(listOf("$rootDir/src/main/java")) }
        resources { setSrcDirs(listOf("$rootDir/src/main/resources")) }
    }
    test {
        java { setSrcDirs(listOf("$rootDir/src/test/java")) }
        resources { setSrcDirs(listOf("$rootDir/src/test/resources")) }
    }
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
        mavenBom("org.springframework.boot:spring-boot-dependencies:2.7.18")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2021.0.9")
    }
}

dependencies {
    api("org.springframework.cloud:spring-cloud-starter-openfeign")

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
            artifactId = "storage-api-client-spring2"
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
