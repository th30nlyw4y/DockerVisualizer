package com.th30nlyw4y.ui;

import com.th30nlyw4y.docker.LogStreamer;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {
    private JPanel closeButtonPanel;
    private JButton closeButton;
    private JScrollPane logAreaPanel;
    private JTextArea logArea;
    private LogStreamer logStreamer;

    public LogPanel() {
        super(new BorderLayout());

        // Create panel with close button
        closeButtonPanel = new JPanel(new BorderLayout());
        closeButton = new JButton("Close");
        closeButton.addActionListener(e -> endLogStreamingAndHide());
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

    public void endLogStreamingAndHide() {
        logStreamer.close();
        setVisible(false);
        invalidate();
    }

    public void startLogStreamingAndShow(String containerId) {
        if (logStreamer != null)
            logStreamer.close(); // Close current log streamer and start a new one
        logStreamer = new LogStreamer(logArea, containerId);
        logArea.setText(null);
        logStreamer.execute();
        if (!isVisible()) setVisible(true);
        invalidate();
    }

    public void stopIfCurrentlyStreamed(String containerId) {
        if (logStreamer != null && !logStreamer.isCurrentlyStreamed(containerId)) return;
        endLogStreamingAndHide();
    }
}
