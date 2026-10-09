package com.rainbowHospitals;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.rainbowHospitals.rainbowHospitals.config.SendGridProperties;

@SpringBootApplication
@EnableConfigurationProperties(SendGridProperties.class)
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
