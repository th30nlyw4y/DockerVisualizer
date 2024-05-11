package com.th30nlyw4y.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ContainersPanel extends JScrollPane {
    private JTable cTable;
    private TableModel cTableModel;

    public ContainersPanel(String[] columns) {
        super();
        initTable(columns);
        setViewportView(cTable);
    }

    private void initTable(String[] columns) {
        cTableModel = new DefaultTableModel();
        ((DefaultTableModel) cTableModel).setColumnIdentifiers(columns);
        cTable = new JTable(cTableModel);
        cTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public JTable getTable() {
        return cTable;
    }

    public String getSelectedContainerId() {
        int col = cTable.getColumn("Id").getModelIndex();
        int row = cTable.getSelectedRow();
        if (row == -1) return null;
        return (String) cTableModel.getValueAt(row, col);
    }
}
