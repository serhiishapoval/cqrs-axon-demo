package com.cqrs.query;

import com.cqrs.query.config.SerializerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import({SerializerConfiguration.class})
public class QueryApplication {

  public static void main(String[] args) {
    SpringApplication.run(QueryApplication.class, args);
  }
}
