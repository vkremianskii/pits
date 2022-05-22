package com.github.vkremianskii.pits.frontends;

import com.github.vkremianskii.pits.frontends.grpc.EquipmentClient;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.Box.createRigidArea;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class Application {
    private static final String OSM_USER_AGENT = "pits/1.0-SNAPSHOT";

    public static void main(String[] args) {
        final var client = new EquipmentClient();
        client.start();

        bootstrapUI(client);
    }

    private static void bootstrapUI(EquipmentClient client) {
        final var simulationPanel = bootstrapSimulationPanel(client);
        final var mapPanel = bootstrapMapPanel();

        final var mainPanel = new JPanel();
        final var mainLayout = new BoxLayout(mainPanel, X_AXIS);
        mainPanel.setLayout(mainLayout);
        mainPanel.add(simulationPanel);
        mainPanel.add(mapPanel);

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

    private static JPanel bootstrapSimulationPanel(EquipmentClient client) {
        final var equipmentIdLabel = new JLabel("Equipment ID");
        final var equipmentIdSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        equipmentIdSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, equipmentIdSpinner.getPreferredSize().height));

        final var latitudeLabel = new JLabel("Latitude");
        final var latitudeSpinner = new JSpinner(new SpinnerNumberModel(41.1494512, -90.0, 90.0, 0.001));
        latitudeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, latitudeSpinner.getPreferredSize().height));

        final var longitudeLabel = new JLabel("Longitude");
        final var longitudeSpinner = new JSpinner(new SpinnerNumberModel(-8.6107884, -180.0, 180.0, 0.001));
        longitudeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, longitudeSpinner.getPreferredSize().height));

        final var elevationLabel = new JLabel("Elevation");
        final var elevationSpinner = new JSpinner(new SpinnerNumberModel(86, 0, 4000, 1));
        elevationSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, elevationSpinner.getPreferredSize().height));

        final var payloadLabel = new JLabel("Payload");
        final var payloadSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 450, 1));
        payloadSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, payloadSpinner.getPreferredSize().height));

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

        final var simulationPanel = new JPanel();
        final var simulationLayout = new BoxLayout(simulationPanel, Y_AXIS);
        simulationPanel.setLayout(simulationLayout);
        simulationPanel.setBorder(createEmptyBorder(3, 3, 3, 3));
        simulationPanel.add(equipmentIdLabel);
        simulationPanel.add(equipmentIdSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(latitudeLabel);
        simulationPanel.add(latitudeSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(longitudeLabel);
        simulationPanel.add(longitudeSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(elevationLabel);
        simulationPanel.add(elevationSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(payloadLabel);
        simulationPanel.add(payloadSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(sendPositionButton);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(sendPayloadButton);

        return simulationPanel;
    }

    private static JPanel bootstrapMapPanel() {
        final var map = new JMapViewerTree("");
        final var tileLoader = (OsmTileLoader) map.getViewer().getTileController().getTileLoader();
        tileLoader.headers.put("User-Agent", OSM_USER_AGENT);

        final var mapPanel = new JPanel();
        mapPanel.add(map);

        return mapPanel;
    }
}
