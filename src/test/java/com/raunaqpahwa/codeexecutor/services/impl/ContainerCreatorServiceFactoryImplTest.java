package com.raunaqpahwa.codeexecutor.services.impl;

import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ContainerCreatorServiceFactoryImplTest {

    @Mock
    private Map<String, ContainerCreatorService> containerCreatorServiceMap;

    @InjectMocks
    private ContainerCreatorServiceFactoryImpl containerCreatorServiceFactory;

    @Test
    @DisplayName("Happy path: The container is created")
    public void testCreateContainer() throws Exception {
        var containerCreatorService = mock(ContainerCreatorService.class);
        when(containerCreatorServiceMap.get(DockerContainer.Language.PYTHON.name())).thenReturn(containerCreatorService);
        containerCreatorServiceFactory.createContainer(DockerContainer.Language.PYTHON.name());

        verify(containerCreatorService).createContainer();
    }

    @Test
    @DisplayName("Error path: The container isn't created")
    public void testCreateContainerNotCreated() {
        var language = "Golang";
        when(containerCreatorServiceMap.get(language)).thenReturn(null);
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> containerCreatorServiceFactory.createContainer(language),
                Constants.LANGUAGE_UNSUPPORTED);
    }
}
