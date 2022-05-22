package com.github.vkremianskii.pits.frontends;

import com.github.vkremianskii.pits.frontends.grpc.EquipmentClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.awt.Component.CENTER_ALIGNMENT;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class Application {

    public static void main(String[] args) {
        final var client = new EquipmentClient();
        client.start();

        final var equipmentIdLabel = new JLabel("Equipment ID");
        equipmentIdLabel.setAlignmentX(CENTER_ALIGNMENT);
        final var equipmentIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        final var latitudeLabel = new JLabel("Latitude");
        latitudeLabel.setAlignmentX(CENTER_ALIGNMENT);
        final var latitudeSpinner = new JSpinner(new SpinnerNumberModel(41.1494512, -90.0, 90.0, 0.001));

        final var longitudeLabel = new JLabel("Longitude");
        longitudeLabel.setAlignmentX(CENTER_ALIGNMENT);
        final var longitudeSpinner = new JSpinner(new SpinnerNumberModel(-8.6107884, -180.0, 180.0, 0.001));

        final var elevationLabel = new JLabel("Elevation");
        elevationLabel.setAlignmentX(CENTER_ALIGNMENT);
        final var elevationSpinner = new JSpinner(new SpinnerNumberModel(86, 0, 4000, 1));

        final var payloadLabel = new JLabel("Payload");
        payloadLabel.setAlignmentX(CENTER_ALIGNMENT);
        final var payloadSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 450, 1));

        final var sendPositionButton = new JButton("Send position");
        sendPositionButton.addActionListener(e -> client.sendPositionChanged(
                (int) equipmentIdSpinner.getValue(),
                (double) latitudeSpinner.getValue(),
                (double) longitudeSpinner.getValue(),
                (int) elevationSpinner.getValue()));

        final var sendPayloadButton = new JButton("Send payload");
        sendPayloadButton.addActionListener(e -> client.sendPayloadChanged(
                (int) equipmentIdSpinner.getValue(),
                (int) payloadSpinner.getValue()));

        final var buttonsPanel = new JPanel();
        final var buttonsLayout = new BoxLayout(buttonsPanel, X_AXIS);
        buttonsPanel.setLayout(buttonsLayout);
        buttonsPanel.setBorder(createEmptyBorder(3, 3, 3, 3));
        buttonsPanel.add(sendPositionButton);
        buttonsPanel.add(sendPayloadButton);
        buttonsPanel.setPreferredSize(new Dimension(400, buttonsPanel.getPreferredSize().height));

        final var mainPanel = new JPanel();
        final var mainLayout = new BoxLayout(mainPanel, Y_AXIS);
        mainPanel.setLayout(mainLayout);
        mainPanel.setBorder(createEmptyBorder(3, 3, 3, 3));
        mainPanel.add(equipmentIdLabel);
        mainPanel.add(equipmentIdSpinner);
        mainPanel.add(latitudeLabel);
        mainPanel.add(latitudeSpinner);
        mainPanel.add(longitudeLabel);
        mainPanel.add(longitudeSpinner);
        mainPanel.add(elevationLabel);
        mainPanel.add(elevationSpinner);
        mainPanel.add(payloadLabel);
        mainPanel.add(payloadSpinner);
        mainPanel.add(buttonsPanel);

        final var frame = new JFrame("Frontends");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.add(mainPanel);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    client.shutdown();
                } catch (InterruptedException ignored) {
                }
            }
        });
    }
}
