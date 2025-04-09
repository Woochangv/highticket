package com.woochang.highticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.woochang.highticket")
public class HighTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighTicketApplication.class, args);
	}

}
