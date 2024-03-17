package com.raunaqpahwa.codeexecutor.configs;

import com.github.dockerjava.api.DockerClient;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
public class SetupBaseImages {

    private static final Logger logger = LoggerFactory.getLogger(SetupBaseImages.class);

    private static final String LOCATION_PATTERN = "classpath:docker-images/*.tar";

    private final DockerClient client;

    private final ResourcePatternResolver resourcePatternResolver;

    private final ApplicationContext applicationContext;

    SetupBaseImages(DockerClient client, ResourcePatternResolver resourcePatternResolver, ApplicationContext applicationContext) {
        this.client = client;
        this.resourcePatternResolver = resourcePatternResolver;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void pullBaseImages() {
        try {
            var images = resourcePatternResolver.getResources(LOCATION_PATTERN);
            for (var image: images) {
                File imageFile = image.getFile();
                logger.info("Loading image {}", imageFile.getName());
                client.loadImageCmd(Files.newInputStream(imageFile.toPath())).exec();
            }
        } catch (IOException e) {
            logger.error("Startup failed, could not load docker images");
            logger.error("Exception: {}", e.getMessage());
            SpringApplication.exit(applicationContext, () -> 1);
        }
    }
}
