
package com.example.vendor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VendorApplication {
    public static void main(String[] args) {
        SpringApplication.run(VendorApplication.class, args);
    }
}
