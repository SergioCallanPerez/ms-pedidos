plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id ("jacoco")
    id ("org.sonarqube") version "4.4.1.3373"
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2024.0.0")
    }
}

jacoco {
    toolVersion = "0.8.11"
}

sonarqube {
    properties {
        property("sonar.projectKey", "SergioCallanPerez_ms-pedidos")
        property("sonar.organization", "sergiocallanperez")
        property("sonar.host.url", "https://sonarcloud.io")

        // Ubicación del reporte JaCoCo
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}


tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    //Springboot
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    //Postgre
    implementation("org.postgresql:r2dbc-postgresql:1.0.5.RELEASE")

    //Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    //.env
    implementation("me.paulschwarz:spring-dotenv:3.0.0")

    //Config Server- Client
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    //Eureka Client
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    //Oauth
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    //Actuator
    implementation ("org.springframework.boot:spring-boot-starter-actuator")

    //Prometheus
    implementation ("io.micrometer:micrometer-registry-prometheus")

    implementation ("io.opentelemetry:opentelemetry-exporter-otlp:1.35.0")
	implementation ("io.micrometer:micrometer-tracing-bridge-otel")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
