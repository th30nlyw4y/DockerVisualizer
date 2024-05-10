package com.th30nlyw4y.ui;

import javax.swing.*;

public class LogPanel extends JScrollPane {
    private JTextArea textArea;

    public LogPanel() {
        super();
        initLogPanel();
        setViewportView(textArea);
        setVisible(false);
    }

    private void initLogPanel() {
        textArea = new JTextArea();
        textArea.setEditable(false);
    }

    public void appendLog(String text) {
        textArea.append(text + "\n");
    }
}
