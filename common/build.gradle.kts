plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j:4.0.6")
    implementation("org.springframework.boot:spring-boot-starter-webmvc:4.0.6")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}
