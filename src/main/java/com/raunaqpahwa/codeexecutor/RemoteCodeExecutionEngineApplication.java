package com.raunaqpahwa.codeexecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RemoteCodeExecutionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemoteCodeExecutionEngineApplication.class, args);
	}

}
