plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":common"))
    implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("com.opencsv:opencsv:5.9")
    implementation("eu.michael-simons.neo4j:neo4j-migrations-spring-boot-starter:3.3.0")
    implementation("dev.langchain4j:langchain4j:1.15.1")
    implementation("dev.langchain4j:langchain4j-ollama:1.15.1")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-data-neo4j-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
