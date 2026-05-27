package com.uni_graph.retrieval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.uni_graph")
public class RetrievalApplication {
  public static void main(String[] args) {
    SpringApplication.run(RetrievalApplication.class, args);
  }
}
