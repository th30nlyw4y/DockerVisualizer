package com.th30nlyw4y.ui;

import javax.swing.*;

public class LogPanel extends JScrollPane {
    private JTextArea logPanel;

    public LogPanel() {
        super();
        initLogPanel();
        setViewportView(logPanel);
        setVisible(false);
    }

    private void initLogPanel() {
        logPanel = new JTextArea();
        logPanel.setEditable(false);
    }
}
