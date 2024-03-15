package com.raunaqpahwa.codeexecutor.configs;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.raunaqpahwa.codeexecutor.models.ImageInfo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SetupBaseImages {

    private static final Logger logger = LoggerFactory.getLogger(SetupBaseImages.class);
    private final DockerClient client;
    private static final List<ImageInfo> images = List.of(ImageInfo.JAVA, ImageInfo.JAVASCRIPT, ImageInfo.PYTHON);

    SetupBaseImages(DockerClient client) {
        this.client = client;
    }

    @PostConstruct
    public void pullBaseImages() {
        logger.info("Pulling base container images");
        try {
            for (var image: images) {
                logger.info("Pulling image for {} version {}", image.name(), image.getTag());
                client.pullImageCmd(image.getName()).withTag(image.getTag()).exec(new PullImageResultCallback())
                        .awaitCompletion();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Could not pull base images, cannot initialise application");
        }
    }
}
