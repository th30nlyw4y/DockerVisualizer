package com.th30nlyw4y.ui;

import com.th30nlyw4y.model.ContainerProperty;
import com.th30nlyw4y.model.ContainerState;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ContainersPanel extends JScrollPane {
    private JTable cTable;
    private TableModel cTableModel;
    private final ContainerProperty[] requiredColumns = {ContainerProperty.Id};

    public ContainersPanel() {
        this(ContainerProperty.Image, ContainerProperty.State);
    }

    public ContainersPanel(ContainerProperty... optionalColumns) {
        super();
        initContainersTable(optionalColumns);
        setViewportView(cTable);
    }

    private void initContainersTable(ContainerProperty[] optionalColumns) {
        ContainerProperty[] columns = ArrayUtils.addAll(requiredColumns, optionalColumns);
        cTableModel = new ContainersTableModel(columns);
        cTable = new JTable(cTableModel);
        cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public void addTableModelListener(TableModelListener l) {
        cTableModel.addTableModelListener(l);
    }

    public void addSelectionListener(ListSelectionListener l) {
        cTable.getSelectionModel().addListSelectionListener(l);
    }

    public String getSelectedContainerId() {
        int col = cTable.getColumn(ContainerProperty.Id.name()).getModelIndex();
        int row = cTable.getSelectedRow();
        if (row < 0 || row >= cTableModel.getRowCount()) return null;
        return (String) cTableModel.getValueAt(row, col);
    }

    public Boolean isRunning(String containerId) {
        return ((ContainersTableModel) cTableModel).getContainerById(containerId)
            .getState()
            .equals(ContainerState.RUNNING.value());
    }

    public Boolean isAffectedByUpdate(int startRow, int endRow) {
        String containerId = getSelectedContainerId();
        if (containerId != null) {
            int selectedContainerRow = ((ContainersTableModel) cTableModel).getRowByContainerId(containerId);
            return selectedContainerRow >= startRow && selectedContainerRow <= endRow;
        }
        return false;
    }

    public TableModel getTableModel() {
        return cTableModel;
    }
}
