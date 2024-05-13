package com.th30nlyw4y.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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
        closeButtonPanel.add(closeButton, BorderLayout.EAST);

        // Create panel with log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setColumns(40);
        logAreaPanel = new JScrollPane(logArea);

        // Put all components to the log panel
        add(closeButtonPanel, BorderLayout.NORTH);
        add(logAreaPanel, BorderLayout.CENTER);

        // Should not be shown until user wants to tail logs
        setVisible(false);
    }

    private void clear() {
        logArea.setText(null);
    }

    public void addCloseButtonListener(ActionListener l) {
        closeButton.addActionListener(l);
    }

    public JTextArea getLogArea() {
        return logArea;
    }

    public void setInvisible() {
        setVisible(false);
        invalidate();
    }

    public void setVisible() {
        clear();
        setVisible(true);
        invalidate();
    }
}
