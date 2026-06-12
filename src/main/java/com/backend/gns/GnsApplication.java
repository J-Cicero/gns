package com.backend.gns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class GnsApplication {

  public static void main(String[] args) {
    SpringApplication.run(GnsApplication.class, args);
  }
}
