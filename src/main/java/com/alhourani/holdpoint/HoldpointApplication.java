package com.alhourani.holdpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class HoldpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(HoldpointApplication.class, args);
	}

}
