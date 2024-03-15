package com.raunaqpahwa.codeexecutor.configs;

import com.github.dockerjava.api.model.Container;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

@Configuration
public class ContainerQueueConfig {

    @Bean
    public Queue<Container> createContainerQueue() {
        return new PriorityQueue<>(Comparator.comparing(Container::getCreated));
    }
}
