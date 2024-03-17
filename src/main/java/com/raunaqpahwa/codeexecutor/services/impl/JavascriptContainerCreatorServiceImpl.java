package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.models.ImageInfo;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorService;
import org.springframework.stereotype.Service;

@Service(Constants.JAVASCRIPT)
public class JavascriptContainerCreatorServiceImpl extends ContainerCreatorService {

    private static final long MAX_MEMORY = 100 * 1024 * 1024;

    private static final String IMAGE_NAME = ImageInfo.JAVASCRIPT.getNameWithTag();

    private static final String WORK_DIR = Constants.JAVASCRIPT_WORK_DIR;

    private static final DockerContainer.Language LANGUAGE = DockerContainer.Language.JAVASCRIPT;

    private static final int MAX_CONTAINERS = 10;

    JavascriptContainerCreatorServiceImpl(DockerClient client) {
        super(client, MAX_CONTAINERS);
    }

    @Override
    public String getImageName() {
        return IMAGE_NAME;
    }

    @Override
    public String getWorkDir() {
        return WORK_DIR;
    }

    @Override
    public long getMaxMemory() {
        return MAX_MEMORY;
    }

    @Override
    public DockerContainer.Language getLanguage() {
        return LANGUAGE;
    }
}
