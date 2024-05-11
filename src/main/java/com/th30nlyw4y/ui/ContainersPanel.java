package com.th30nlyw4y.ui;

import com.github.dockerjava.api.DockerClient;
import com.th30nlyw4y.docker.StateManager;
import com.th30nlyw4y.model.ContainerProperty;
import com.th30nlyw4y.model.ContainerState;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

public class ContainersPanel extends JScrollPane {
    private JTable cTable;
    private TableModel cTableModel;
    private StateManager stateManager;
    private final ContainerProperty[] requiredColumns = {ContainerProperty.Id};

    public ContainersPanel() {
        this(null, new ContainerProperty[]{ContainerProperty.Image, ContainerProperty.State});
    }

    public ContainersPanel(DockerClient dockerClient, ContainerProperty[] optionalColumns) {
        super();
        initTable(optionalColumns);
        setViewportView(cTable);
        initAndRunBackgroundUpdate(dockerClient);
    }

    private void initTable(ContainerProperty[] optionalColumns) {
        ContainerProperty[] columns = ArrayUtils.addAll(requiredColumns, optionalColumns);
        cTableModel = new ContainersTableModel(columns);
        cTable = new JTable(cTableModel);
        cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void initAndRunBackgroundUpdate(DockerClient dockerClient) {
        stateManager = dockerClient != null ? new StateManager(dockerClient, (ContainersTableModel) cTableModel) :
            new StateManager((ContainersTableModel) cTableModel);
        stateManager.execute();
    }

    public void addSelectionListener(ListSelectionListener l) {
        cTable.getSelectionModel().addListSelectionListener(l);
    }

    public String getSelectedContainerId() {
        int col = cTable.getColumn(ContainerProperty.Id.name()).getModelIndex();
        int row = cTable.getSelectedRow();
        if (row == -1) return null;
        return (String) cTableModel.getValueAt(row, col);
    }

    public Boolean isRunning(String containerId) {
        return ((ContainersTableModel) cTableModel).getContainerById(containerId)
            .getState()
            .equals(ContainerState.RUNNING.value());
    }
}
