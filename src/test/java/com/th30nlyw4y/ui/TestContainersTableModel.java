package com.th30nlyw4y.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.*;
import com.th30nlyw4y.model.ContainerProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@TestInstance(Lifecycle.PER_CLASS)
public class TestContainersTableModel {
    ContainerProperty[] columns;
    ContainersTableModel tableModel;
    List<Container> containers;

    @BeforeAll
    void initAll() throws IOException {
        BufferedReader r = Files.newBufferedReader(
            Path.of(getClass().getClassLoader().getResource("containers.json").getPath())
        );
        ObjectMapper mapper = new ObjectMapper();
        this.containers = mapper.readValue(r, new TypeReference<>() {
        });
        this.columns = new ContainerProperty[]{
            ContainerProperty.Image,
            ContainerProperty.State,
            ContainerProperty.Command,
            ContainerProperty.Created,
            ContainerProperty.ImageID,
            ContainerProperty.Names,
            ContainerProperty.Ports,
            ContainerProperty.Labels,
            ContainerProperty.Status,
            ContainerProperty.SizeRw,
            ContainerProperty.SizeRootFs,
            ContainerProperty.HostConfig,
            ContainerProperty.NetworkSettings,
            ContainerProperty.Mounts
        };
    }

    Container getRandomContainer() {
        int idx = new Random().nextInt(0, containers.size());
        return containers.get(idx);
    }

    @BeforeEach
    void init() {
        this.tableModel = new ContainersTableModel(columns);
    }

    @Test
    void testGetTableSizeMethods() {
        assertAll(
            () -> assertEquals(columns.length, tableModel.getColumnCount()),
            () -> assertEquals(0, tableModel.getRowCount())
        );
    }

    @Test
    void testAddNullContainer() {
        assertDoesNotThrow(() -> tableModel.addContainer(null));
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testAddContainer() {
        for (Container c : containers) {
            assertDoesNotThrow(() -> tableModel.addContainer(c));
            String id = c.getId();
            String state = c.getState();
            String cmd = c.getCommand();
            Long created = c.getCreated();
            String imageId = c.getImageId();
            String[] names = c.getNames();
            ContainerPort[] ports = c.getPorts();
            Map<String, String> labels = c.getLabels();
            String status = c.getStatus();
            Long sizeRw = c.getSizeRw();
            Long sizeRootFs = c.getSizeRootFs();
            ContainerHostConfig hostConfig = c.getHostConfig();
            ContainerNetworkSettings networkSettings = c.getNetworkSettings();
            List<ContainerMount> mounts = c.getMounts();
            Container addedContainer = tableModel.getContainerById(c.getId());
            assertAll(
                () -> assertEquals(id, addedContainer.getId()),
                () -> assertEquals(state, addedContainer.getState()),
                () -> assertEquals(cmd, addedContainer.getCommand()),
                () -> assertEquals(created, addedContainer.getCreated()),
                () -> assertEquals(imageId, addedContainer.getImageId()),
                () -> assertEquals(names, addedContainer.getNames()),
                () -> assertEquals(ports, addedContainer.getPorts()),
                () -> assertEquals(labels, addedContainer.getLabels()),
                () -> assertEquals(status, addedContainer.getStatus()),
                () -> assertEquals(sizeRw, addedContainer.getSizeRw()),
                () -> assertEquals(sizeRootFs, addedContainer.getSizeRootFs()),
                () -> assertEquals(hostConfig, addedContainer.getHostConfig()),
                () -> assertEquals(networkSettings, addedContainer.getNetworkSettings()),
                () -> assertEquals(mounts, addedContainer.getMounts())
            );
        }
        assertEquals(containers.size(), tableModel.getRowCount());
    }

    @Test
    void testRemoveContainerByIdNotExists() {
        assertDoesNotThrow(() -> tableModel.removeContainer(getRandomContainer().getId()));
    }

    @Test
    void testRemoveContainerByIdOnlyOneContainerExists() {
        Container container = getRandomContainer();
        tableModel.addContainer(container);
        assertEquals(1, tableModel.getRowCount());
        assertDoesNotThrow(() -> tableModel.removeContainer(container.getId()));
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testRemoveContainerById() {
        for (Container c : containers) {
            tableModel.addContainer(c);
        }
        assertEquals(containers.size(), tableModel.getRowCount());
        for (int i = 0; i < containers.size(); i++) {
            tableModel.removeContainer(containers.get(i).getId());
            int expectedRowCount = containers.size() - i - 1;
            assertEquals(expectedRowCount, tableModel.getRowCount());
        }
    }

    @Test
    void testUpdateNullContainer() {
        assertDoesNotThrow(() -> tableModel.updateContainer(null));
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testUpdateNonExistentContainer() {
        assertDoesNotThrow(() -> tableModel.updateContainer(containers.getFirst()));
        assertEquals(0, tableModel.getRowCount());
    }

    @Test
    void testUpdateContainer() {
        Container c = getRandomContainer();
        assertDoesNotThrow(() -> tableModel.addContainer(c));
        assertEquals(1, tableModel.getRowCount());
        assertDoesNotThrow(() -> tableModel.updateContainer(c));
        assertEquals(1, tableModel.getRowCount());
    }

    @Test
    void testGetContainerByNullId() {
        Container c = assertDoesNotThrow(() -> tableModel.getContainerById(null));
        assertNull(c);
    }

    @Test
    void testGetContainerByNonExistentId() {
        Container c = assertDoesNotThrow(() -> tableModel.getContainerById(containers.getFirst().getId()));
        assertNull(c);
    }

    @Test
    void testGetContainerById() {
        Container c = getRandomContainer();
        tableModel.addContainer(c);
        assertEquals(1, tableModel.getRowCount());
        Container extractedContainer = assertDoesNotThrow(() -> tableModel.getContainerById(c.getId()));
        assertEquals(c, extractedContainer);
    }

    @Test
    void testGetRowByNullContainerId() {
        int result = assertDoesNotThrow(() -> tableModel.getRowByContainerId(null));
        assertEquals(-1, result);
    }

    @Test
    void testGetRowByNonExistentContainerId() {
        Container c = getRandomContainer();
        int result = assertDoesNotThrow(() -> tableModel.getRowByContainerId(c.getId()));
        assertEquals(-1, result);
    }

    @Test
    void testGetRowByContainerId() {
        Container c = getRandomContainer();
        tableModel.addContainer(c);
        assertEquals(1, tableModel.getRowCount());
        int result = assertDoesNotThrow(() -> tableModel.getRowByContainerId(c.getId()));
        assertEquals(0, result);
    }
}
