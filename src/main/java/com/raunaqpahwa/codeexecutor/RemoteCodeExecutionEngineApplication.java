package com.raunaqpahwa.codeexecutor;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RemoteCodeExecutionEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemoteCodeExecutionEngineApplication.class, args);
	}

}
