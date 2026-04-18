package com.example.FullCount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example")
@EnableJpaRepositories(basePackages = "com.example")
@EntityScan(basePackages = "com.example")
@EnableJpaAuditing
@EnableScheduling //스케줄러 활성화
public class FullCountApplication {

	public static void main(String[] args) {
		SpringApplication.run(FullCountApplication.class, args);
	}
}
