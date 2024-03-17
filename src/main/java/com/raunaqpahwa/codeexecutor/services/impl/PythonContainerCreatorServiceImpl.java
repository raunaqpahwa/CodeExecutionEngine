package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.models.ImageInfo;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorService;
import org.springframework.stereotype.Service;

@Service(Constants.PYTHON)
public class PythonContainerCreatorServiceImpl extends ContainerCreatorService {

    private static final long MAX_MEMORY = 100 * 1024 * 1024;

    private static final String IMAGE_NAME = ImageInfo.PYTHON.getNameWithTag();

    private static final String WORK_DIR = Constants.PYTHON_WORK_DIR;

    private static final DockerContainer.Language LANGUAGE = DockerContainer.Language.PYTHON;

    private static final int MAX_CONTAINERS = 10;

    PythonContainerCreatorServiceImpl(DockerClient client) {
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
