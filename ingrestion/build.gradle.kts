plugins {
	java
	id("org.springframework.boot") version "4.0.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.uni-graph"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["springAiVersion"] = "2.0.0-M3"

dependencies {
    var neo4jMigrationsVersion = "3.3.0"

	implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation ("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
	// implementation("org.springframework.ai:spring-ai-jsoup-document-reader")
	// implementation("org.springframework.ai:spring-ai-starter-vector-store-neo4j")
    implementation ("eu.michael-simons.neo4j:neo4j-migrations-spring-boot-starter:$neo4jMigrationsVersion")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	// developmentOnly("org.springframework.ai:spring-ai-spring-boot-docker-compose")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-data-neo4j-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
