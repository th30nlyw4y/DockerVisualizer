package com.th30nlyw4y.ui;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private JPanel closeButtonPanel;
    private JButton closeButton;
    private JScrollPane logAreaPanel;
    private JTextArea logArea;

    public LogPanel() {
        super(new BorderLayout());

        // Create panel with close button
        closeButtonPanel = new JPanel(new BorderLayout());
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> closeButtonHandler());
        closeButtonPanel.add(closeButton, BorderLayout.EAST);

        // Create panel with log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logAreaPanel = new JScrollPane(logArea);

        // Put all components to the log panel
        add(closeButtonPanel, BorderLayout.NORTH);
        add(logAreaPanel, BorderLayout.CENTER);

        // Should not be shown until user wants to tail logs
        setVisible(false);
    }

    private void closeButtonHandler() {
        setInvisible();
    }

    public void appendLog(String text) {
        logArea.append(text + "\n");
    }

    public void setInvisible() {
        setVisible(false);
        getParent().validate();
    }

    public void setVisible() {
        setVisible(true);
        getParent().validate();
    }

    public void clear() {
        logArea.setText(null);
    }
}
