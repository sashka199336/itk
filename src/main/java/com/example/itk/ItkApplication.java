package com.example.itk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry; // импортируем аннотацию

@SpringBootApplication
@EnableRetry
public class ItkApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItkApplication.class, args);
	}

}