package com.raunaqpahwa.codeexecutor.services.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.raunaqpahwa.codeexecutor.exceptions.ContainerNotCreatedException;
import com.raunaqpahwa.codeexecutor.models.Constants;
import com.raunaqpahwa.codeexecutor.models.DockerContainer;
import com.raunaqpahwa.codeexecutor.services.ContainerCreatorServiceFactory;
import com.raunaqpahwa.codeexecutor.services.ContainerRemovalService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ContainerManagerServiceImplTest {

    @InjectMocks
    private ContainerManagerServiceImpl containerManagerService;

    @Mock
    private DockerClient client;

    @Mock
    private ContainerCreatorServiceFactory containerCreatorServiceFactory;

    @Mock
    private ContainerRemovalService containerRemovalService;

    @Mock
    private Queue<DockerContainer> containerQueue;

    @Test
    @DisplayName("Happy path: Test create container")
    public void testCreateContainer() throws Exception {
        var dockerContainer = mock(DockerContainer.class);
        when(containerCreatorServiceFactory.createContainer(DockerContainer.Language.PYTHON.name())).thenReturn(dockerContainer);
        when(containerQueue.offer(dockerContainer)).thenReturn(true);
        when(containerQueue.size()).thenReturn(1);

        var createdContainer = containerManagerService.createContainer(DockerContainer.Language.PYTHON.name());
        Assertions.assertEquals(1, containerQueue.size());
        Assertions.assertEquals(dockerContainer, createdContainer);
        verify(containerQueue).offer(dockerContainer);
        verify(containerCreatorServiceFactory).createContainer(DockerContainer.Language.PYTHON.name());
    }

    @Test
    @DisplayName("Error path: Unable to create container")
    public void testCreateContainerError() throws Exception {
        when(containerCreatorServiceFactory.createContainer(DockerContainer.Language.PYTHON.name()))
                .thenThrow(new ContainerNotCreatedException(Constants.CONTAINER_NOT_CREATED_EXCEPTION));
        Assertions.assertThrowsExactly(ContainerNotCreatedException.class,
                () -> containerManagerService.createContainer(DockerContainer.Language.PYTHON.name()),
                Constants.CONTAINER_NOT_CREATED_EXCEPTION);
    }

    @Test
    @DisplayName("Happy path: Container older than 30s is removed")
    public void testRemoveContainers() {
        var container = mock(Container.class);
        var dockerContainer = mock(DockerContainer.class);

        when(containerQueue.size()).thenReturn(1);
        when(containerQueue.peek()).thenReturn(dockerContainer);
        when(containerQueue.poll()).thenReturn(dockerContainer);
        when(dockerContainer.getContainer()).thenReturn(container);
        when(dockerContainer.getContainer().getCreated()).thenReturn(1713053680L);
        when(containerRemovalService.stopAndRemoveContainer(dockerContainer.getContainer()))
                .thenReturn(CompletableFuture.completedFuture(true));

        containerManagerService.stopAndRemoveContainers();
        verify(containerCreatorServiceFactory).incrementAvailableContainers(dockerContainer);
    }

    @Test
    @DisplayName("Error path: Container older than 30s unable to be removed")
    public void testRemoveContainersUnableToRemove() {
        var container = mock(Container.class);
        var dockerContainer = mock(DockerContainer.class);

        when(containerQueue.size()).thenReturn(1);
        when(containerQueue.peek()).thenReturn(dockerContainer);
        when(containerQueue.poll()).thenReturn(dockerContainer);
        when(dockerContainer.getContainer()).thenReturn(container);
        when(dockerContainer.getContainer().getCreated()).thenReturn(1713053680L);
        when(containerRemovalService.stopAndRemoveContainer(dockerContainer.getContainer()))
                .thenReturn(CompletableFuture.completedFuture(false));

        containerManagerService.stopAndRemoveContainers();
        verify(containerQueue, times(1)).peek();
        verify(containerQueue, times(1)).poll();
        verify(containerQueue).offer(dockerContainer);
    }

    @Test
    @DisplayName("Happy path: Ghost containers are removed")
    public void testStopAndRemoveGhostContainers() {
        var container = mock(Container.class);
        var listContainersCmd = mock(ListContainersCmd.class);
        when(client.listContainersCmd()).thenReturn(listContainersCmd);
        when(listContainersCmd.exec()).thenReturn(new ArrayList<>(List.of(container)));
        when(container.getCreated()).thenReturn(1713053680L);

        containerManagerService.stopAndRemoveGhostContainers();
        verify(containerRemovalService).stopAndRemoveContainer(container);
    }
}
