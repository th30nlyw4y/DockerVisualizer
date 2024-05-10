package com.th30nlyw4y.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ContainersPanel extends JScrollPane {
    private JTable containersTable;
    private TableModel containersTableModel;

    public ContainersPanel(String[] columns) {
        super();
        initTable(columns);
        setViewportView(containersTable);
    }

    private void initTable(String[] columns) {
        containersTableModel = new DefaultTableModel();
        ((DefaultTableModel) containersTableModel).setColumnIdentifiers(columns);
        containersTable = new JTable();
        containersTable.setModel(containersTableModel);
    }

    public JTable getTable() {
        return containersTable;
    }
}
