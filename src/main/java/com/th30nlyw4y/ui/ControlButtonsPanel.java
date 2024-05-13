package com.th30nlyw4y.ui;

import com.th30nlyw4y.model.ControlButtonType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlButtonsPanel extends JPanel {
    private JButton startButton;
    private JButton stopButton;
    private JButton logsButton;

    public ControlButtonsPanel() {
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

    public JButton getButton(ControlButtonType t) {
        return switch (t) {
            case START -> startButton;
            case STOP -> stopButton;
            case LOGS -> logsButton;
        };
    }

    public void addButtonListener(ControlButtonType t, ActionListener l) {
        getButton(t).addActionListener(l);
    }

    public void setDisabled(ControlButtonType... buttons) {
        for (ControlButtonType t : buttons) {
            getButton(t).setEnabled(false);
        }
    }

    public void setEnabled(ControlButtonType... buttons) {
        for (ControlButtonType t : buttons) {
            getButton(t).setEnabled(true);
        }
    }
}
