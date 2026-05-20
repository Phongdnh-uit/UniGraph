plugins {
    `java-library`
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j:3.4.3")
    implementation("org.springframework.boot:spring-boot-starter-webmvc:3.4.3")
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}
