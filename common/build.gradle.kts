plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j:4.0.6")
    implementation("org.springframework.boot:spring-boot-starter-webmvc:4.0.6")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
