package com.th30nlyw4y.ui;

import com.github.dockerjava.api.model.Container;
import com.th30nlyw4y.model.ContainerProperty;
import org.apache.hc.core5.annotation.Contract;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContainersTableModel extends AbstractTableModel {
    private ContainerProperty[] columns;
    private Map<String, Integer> indexes;
    private List<Container> state;

    public ContainersTableModel(ContainerProperty... columns) {
        super();

        this.columns = columns;
        // The task is to support 1000 containers, here we have a doubled value just in case
        indexes = new HashMap<>(2000);
        state = new ArrayList<>(2000);
    }

    public void removeContainer(String containerId) {
        if (indexes.containsKey(containerId)) {
            int removedContainerIdx = indexes.get(containerId);
            String lastId = state.getLast().getId();
            state.set(removedContainerIdx, state.getLast());
            indexes.put(lastId, removedContainerIdx);
            state.removeLast();
            indexes.remove(containerId);

            // Notify about changes
            fireTableRowsUpdated(removedContainerIdx, removedContainerIdx);
            fireTableRowsDeleted(state.size(), state.size());
        }
    }

    public void addContainer(Container c) {
        if (c == null) return;
        state.add(c);
        indexes.put(c.getId(), state.size() - 1);
        fireTableRowsInserted(state.size() - 1, state.size() - 1);
    }

    public void updateContainer(Container c) {
        if (c == null || !indexes.containsKey(c.getId())) return;
        int containerIdx = indexes.get(c.getId());
        state.set(containerIdx, c);
        fireTableRowsUpdated(containerIdx, containerIdx);
    }

    public Container getContainerById(String containerId) {
        return containerId != null && indexes.containsKey(containerId) ? state.get(indexes.get(containerId)) : null;
    }

    public int getRowByContainerId(String containerId) {
        return containerId != null && indexes.containsKey(containerId) ? indexes.get(containerId) : -1;
    }

    @Override
    public String getColumnName(int idx) {
        return columns[idx].name();
    }

    @Override
    public int getRowCount() {
        return state.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Container c = state.get(rowIndex);
        return switch (columns[columnIndex]) {
            case Id -> c.getId();
            case Image -> c.getImage();
            case State -> c.getState();
            case Command -> c.getCommand();
            case Created -> c.getCreated();
            case ImageID -> c.getImageId();
            case Names -> c.getNames();
            case Ports -> c.getPorts();
            case Labels -> c.getLabels();
            case Status -> c.getStatus();
            case SizeRw -> c.getSizeRw();
            case SizeRootFs -> c.getSizeRootFs();
            case HostConfig -> c.getHostConfig();
            case NetworkSettings -> c.getNetworkSettings();
            case Mounts -> c.getMounts();
        };
    }
}