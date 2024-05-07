package com.th30nlyw4y.ui;


import com.th30nlyw4y.utils.ButtonType;

import javax.swing.*;
import java.awt.*;

public class ButtonsPanel extends JPanel {
    private final JButton startButton;
    private final JButton stopButton;
    private final JButton logsButton;

    public ButtonsPanel() {
        super();

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        logsButton = new JButton("Logs");

        setLayout(new GridLayout(1, 3));
        add(startButton);
        add(stopButton);
        add(logsButton);
    }

    public JButton getButton(ButtonType t) {
        return switch (t) {
            case START_BUTTON -> startButton;
            case STOP_BUTTON -> stopButton;
            case LOGS_BUTTON -> logsButton;
        };
    }
}
