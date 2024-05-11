package com.th30nlyw4y.ui;

import com.th30nlyw4y.model.ButtonType;

import javax.swing.*;
import java.awt.*;

public class ButtonsPanel extends JPanel {
    private JButton startButton;
    private JButton stopButton;
    private JButton logsButton;

    public ButtonsPanel() {
        super();

        startButton = new JButton("Start");
        startButton.setEnabled(false);
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        logsButton = new JButton("Logs");
        logsButton.setEnabled(false);

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

    public void setDisabled(ButtonType... buttons) {
        for (ButtonType t : buttons) {
            getButton(t).setEnabled(false);
        }
    }

    public void setEnabled(ButtonType... buttons) {
        for (ButtonType t : buttons) {
            getButton(t).setEnabled(true);
        }
    }
}
