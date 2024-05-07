package com.th30nlyw4y.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class ContainersTable extends JTable {
    TableModel containersTableModel;

    public ContainersTable(String[] columns) {
        super();
        this.containersTableModel = new DefaultTableModel();
        ((DefaultTableModel) containersTableModel).setColumnIdentifiers(columns);
        setModel(containersTableModel);
    }
}
