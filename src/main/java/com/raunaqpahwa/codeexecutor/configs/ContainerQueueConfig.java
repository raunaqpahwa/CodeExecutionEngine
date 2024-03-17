package com.raunaqpahwa.codeexecutor.configs;

import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

@Configuration
public class ContainerQueueConfig {

    @Bean
    public Queue<DockerContainer> createContainerQueue() {
        return new PriorityQueue<>(Comparator.comparing(DockerContainer::getRemovalPriority)
                .thenComparing(c -> c.getContainer().getCreated()));
    }
}
