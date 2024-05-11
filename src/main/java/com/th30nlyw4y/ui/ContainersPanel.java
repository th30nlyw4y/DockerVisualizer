package com.th30nlyw4y.ui;


import com.th30nlyw4y.model.ContainerProperty;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import javax.swing.table.TableModel;

public class ContainersPanel extends JScrollPane {
    private JTable cTable;
    private TableModel cTableModel;
    private final ContainerProperty[] requiredColumns = {ContainerProperty.Id};

    public ContainersPanel() {
        this((ContainerProperty) null);
    }

    public ContainersPanel(ContainerProperty... optionalColumns) {
        super();
        ContainerProperty[] columns = ArrayUtils.addAll(requiredColumns, optionalColumns);
        initTable(columns);
        setViewportView(cTable);
    }

    private void initTable(ContainerProperty... columns) {
        cTableModel = new ContainersTableModel(columns);
        cTable = new JTable(cTableModel);
        cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JTable getTable() {
        return cTable;
    }

    public String getSelectedContainerId() {
        int col = cTable.getColumn(ContainerProperty.Id.name()).getModelIndex();
        int row = cTable.getSelectedRow();
        if (row == -1) return null;
        return (String) cTableModel.getValueAt(row, col);
    }
}
