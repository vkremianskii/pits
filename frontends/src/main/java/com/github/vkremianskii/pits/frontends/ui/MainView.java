package com.github.vkremianskii.pits.frontends.ui;

import com.bbn.openmap.proj.Ellipsoid;
import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.UTMPoint;
import com.github.vkremianskii.pits.frontends.grpc.GrpcClient;
import com.github.vkremianskii.pits.registry.client.RegistryClient;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.openstreetmap.gui.jmapviewer.*;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.bbn.openmap.proj.Ellipsoid.WGS_84;
import static com.bbn.openmap.proj.coords.UTMPoint.LLtoUTM;
import static com.bbn.openmap.proj.coords.UTMPoint.UTMtoLL;
import static java.awt.Color.*;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static javax.swing.BorderFactory.*;
import static javax.swing.Box.createRigidArea;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;

public class MainView {
    private static final double DEFAULT_LATITIUDE = 41.1494512;
    private static final double DEFAULT_LONGITUDE = -8.6107884;
    private static final int DEFAULT_ELEVATION = 86;
    private static final int DEFAULT_PAYLOAD = 0;
    private static final int NUM_CIRCLE_SEGMENTS = 32;
    private static final String OSM_USER_AGENT = "pits.frontends/1.0-SNAPSHOT";

    private static final Map<EquipmentState, Color> COLOR_FROM_STATE = Map.of(
            TruckState.EMPTY, new Color(0x90ee90),
            TruckState.WAIT_LOAD, new Color(0xadd8e6),
            TruckState.LOAD, new Color(0x00008b),
            TruckState.HAUL, new Color(0x006400),
            TruckState.UNLOAD, new Color(0x00008b));

    private final RegistryClient registryClient;
    private final GrpcClient grpcClient;
    private final TreeMap<Integer, Equipment> equipmentById = new TreeMap<>();

    private JComboBox<Integer> equipmentIdComboBox;
    private JTextField nameTextField;
    private JTextField typeTextField;
    private JTextField stateTextField;
    private JSpinner latitudeSpinner;
    private JSpinner longitudeSpinner;
    private JSpinner elevationSpinner;
    private JSpinner payloadSpinner;
    private JButton sendPositionButton;
    private JButton sendPayloadButton;
    private JMapViewer mapViewer;

    public MainView(RegistryClient registryClient, GrpcClient grpcClient) {
        this.registryClient = requireNonNull(registryClient);
        this.grpcClient = requireNonNull(grpcClient);
    }

    public void initialize() {
        final var equipmentPanel = bootstrapEquipmentPanel();
        final var mapPanel = bootstrapMapPanel();

        final var mainPanel = new JPanel();
        final var mainLayout = new BoxLayout(mainPanel, X_AXIS);
        mainPanel.setLayout(mainLayout);
        mainPanel.add(equipmentPanel);
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
                    grpcClient.shutdown();
                } catch (InterruptedException ignored) {
                }
            }
        });

        equipmentIdComboBox.addItemListener(e -> {
            if (e.getStateChange() != ItemEvent.SELECTED) {
                return;
            }
            final var equipmentId = (int) e.getItem();
            final var equipment = equipmentById.get(equipmentId);
            final var position = Optional.ofNullable(equipment.getPosition());
            final var state = Optional.ofNullable(equipment.getState());

            int payload = DEFAULT_PAYLOAD;
            if (equipment.getType() == EquipmentType.TRUCK) {
                final var truck = (Truck) equipment;
                if (truck.getPayload() != null) {
                    payload = truck.getPayload();
                }
            }

            nameTextField.setText(equipment.getName());
            typeTextField.setText(equipment.getType().name());
            stateTextField.setText(state.map(EquipmentState::name).orElse(""));
            latitudeSpinner.setValue(position.map(Position::getLatitude).orElse(DEFAULT_LATITIUDE));
            latitudeSpinner.setEnabled(true);
            longitudeSpinner.setValue(position.map(Position::getLongitude).orElse(DEFAULT_LONGITUDE));
            longitudeSpinner.setEnabled(true);
            elevationSpinner.setValue(position.map(Position::getElevation).orElse(DEFAULT_ELEVATION));
            elevationSpinner.setEnabled(true);
            payloadSpinner.setValue(payload);
            payloadSpinner.setEnabled(equipment.getType() == EquipmentType.TRUCK);
            sendPositionButton.setEnabled(true);
            sendPayloadButton.setEnabled(equipment.getType() == EquipmentType.TRUCK);
        });

        sendPositionButton.addActionListener(e -> grpcClient.sendPositionChanged(
                (int) equipmentIdComboBox.getSelectedItem(),
                (double) latitudeSpinner.getValue(),
                (double) longitudeSpinner.getValue(),
                (int) elevationSpinner.getValue()));

        sendPayloadButton.addActionListener(e -> grpcClient.sendPayloadChanged(
                (int) equipmentIdComboBox.getSelectedItem(),
                (int) payloadSpinner.getValue()));

        Flux.interval(Duration.ofMillis(1_500))
                .flatMap(__ -> refreshEquipment())
                .subscribe();
    }

    private JPanel bootstrapEquipmentPanel() {
        final var equipmentIdLabel = new JLabel("Equipment ID");
        equipmentIdComboBox = new JComboBox<>();
        equipmentIdComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, equipmentIdComboBox.getPreferredSize().height));

        final var nameLabel = new JLabel("Name");
        nameTextField = new JTextField("");
        nameTextField.setEnabled(false);

        final var typeLabel = new JLabel("Type");
        typeTextField = new JTextField("");
        typeTextField.setEnabled(false);

        final var stateLabel = new JLabel("State");
        stateTextField = new JTextField("");
        stateTextField.setEnabled(false);

        final var simulationPanel = bootstrapSimulationPanel();

        final var equipmentPanel = new JPanel();
        final var equipmentLayout = new BoxLayout(equipmentPanel, Y_AXIS);
        equipmentPanel.setLayout(equipmentLayout);
        equipmentPanel.setBorder(createEmptyBorder(3, 3, 3, 3));
        equipmentPanel.add(equipmentIdLabel);
        equipmentPanel.add(equipmentIdComboBox);
        equipmentPanel.add(createRigidArea(new Dimension(0, 3)));
        equipmentPanel.add(nameLabel);
        equipmentPanel.add(nameTextField);
        equipmentPanel.add(createRigidArea(new Dimension(0, 3)));
        equipmentPanel.add(typeLabel);
        equipmentPanel.add(typeTextField);
        equipmentPanel.add(createRigidArea(new Dimension(0, 3)));
        equipmentPanel.add(stateLabel);
        equipmentPanel.add(stateTextField);
        equipmentPanel.add(createRigidArea(new Dimension(0, 3)));
        equipmentPanel.add(simulationPanel);

        return equipmentPanel;
    }

    private JPanel bootstrapSimulationPanel() {
        final var latitudeLabel = new JLabel("Latitude");
        latitudeSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_LATITIUDE, -90.0, 90.0, 0.001));
        latitudeSpinner.setEditor(new JSpinner.NumberEditor(latitudeSpinner, "0.000000"));
        latitudeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, latitudeSpinner.getPreferredSize().height));
        latitudeSpinner.setEnabled(false);

        final var longitudeLabel = new JLabel("Longitude");
        longitudeSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_LONGITUDE, -180.0, 180.0, 0.001));
        longitudeSpinner.setEditor(new JSpinner.NumberEditor(longitudeSpinner, "0.000000"));
        longitudeSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, longitudeSpinner.getPreferredSize().height));
        longitudeSpinner.setEnabled(false);

        final var elevationLabel = new JLabel("Elevation");
        elevationSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_ELEVATION, 0, 4000, 1));
        elevationSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, elevationSpinner.getPreferredSize().height));
        elevationSpinner.setEnabled(false);

        final var payloadLabel = new JLabel("Payload");
        payloadSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_PAYLOAD, 0, 500_000, 1_000));
        payloadSpinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, payloadSpinner.getPreferredSize().height));
        payloadSpinner.setEnabled(false);

        sendPositionButton = new JButton("Send position");
        sendPositionButton.setEnabled(false);

        sendPayloadButton = new JButton("Send payload");
        sendPayloadButton.setEnabled(false);

        final var simulationPanel = new JPanel();
        final var simulationLayout = new BoxLayout(simulationPanel, Y_AXIS);
        simulationPanel.setLayout(simulationLayout);
        simulationPanel.setBorder(createCompoundBorder(createTitledBorder("Simulation"), createEmptyBorder(3, 3, 3, 3)));
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

    private JPanel bootstrapMapPanel() {
        final var coordsLabel = new JLabel();

        final var map = new JMapViewerTree("");
        mapViewer = map.getViewer();
        mapViewer.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                final var position = mapViewer.getPosition(e.getPoint());
                coordsLabel.setText(String.format("lat %.06f lon %.06f", position.getLat(), position.getLon()));
            }
        });
        mapViewer.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                final var position = mapViewer.getPosition(e.getPoint());
                latitudeSpinner.setValue(position.getLat());
                longitudeSpinner.setValue(position.getLon());
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        final var tileLoader = (OsmTileLoader) map.getViewer().getTileController().getTileLoader();
        tileLoader.headers.put("User-Agent", OSM_USER_AGENT);

        final var mapPanel = new JPanel();
        final var mapLayout = new BoxLayout(mapPanel, Y_AXIS);
        mapPanel.setLayout(mapLayout);
        mapPanel.add(map);
        mapPanel.add(coordsLabel);

        return mapPanel;
    }

    private Mono<Void> refreshEquipment() {
        return registryClient.getEquipment()
                .doOnNext(equipment -> {
                    final var newEquipmentById = equipment.stream().collect(toMap(Equipment::getId, identity()));
                    if (newEquipmentById.equals(equipmentById)) {
                        return;
                    }
                    equipmentById.clear();
                    equipmentById.putAll(newEquipmentById);
                    SwingUtilities.invokeLater(this::refreshEquipmentControls);
                })
                .then();
    }

    private void refreshEquipmentControls() {
        final var selectedItem = (Integer) equipmentIdComboBox.getSelectedItem();
        equipmentIdComboBox.removeAllItems();
        mapViewer.removeAllMapMarkers();
        mapViewer.removeAllMapPolygons();
        for (final var e : equipmentById.values()) {
            equipmentIdComboBox.addItem(e.getId());
            mapMarkerFromEquipment(e).ifPresent(mapViewer::addMapMarker);
            if (e.getType() == EquipmentType.SHOVEL) {
                loadZoneFromShovel((Shovel) e).ifPresent(mapViewer::addMapPolygon);
            }
        }
        if (selectedItem != null && equipmentById.containsKey(selectedItem)) {
            equipmentIdComboBox.setSelectedItem(selectedItem);
        }
    }

    private static Optional<MapMarkerDot> mapMarkerFromEquipment(Equipment equipment) {
        return Optional.ofNullable(equipment.getPosition())
                .map(position -> {
                    final var marker = new MapMarkerDot(position.getLatitude(), position.getLongitude());
                    marker.setName(equipment.getName() + " (" + equipment.getId() + ")");
                    marker.setBackColor(colorFromState(equipment.getState()));
                    return marker;
                });
    }

    private static Optional<MapPolygon> loadZoneFromShovel(Shovel shovel) {
        return Optional.ofNullable(shovel.getPosition())
                .map(position -> newMapPolygon(position.getLatitude(), position.getLongitude(), shovel.getLoadRadius()));
    }

    private static MapPolygon newMapPolygon(double latitude, double longitude, double radius) {
        final var centerLL = new LatLonPoint.Double(latitude, longitude);
        final var centerUTM = LLtoUTM(centerLL);

        final var points = new ArrayList<Coordinate>();
        for (double angle = 0.0; angle < 2.0 * Math.PI; angle += 2.0 * Math.PI / (double) NUM_CIRCLE_SEGMENTS) {
            final var pointUTM = new UTMPoint(centerUTM);
            pointUTM.northing += radius * Math.sin(angle);
            pointUTM.easting += radius * Math.cos(angle);
            final var pointLL = UTMtoLL(pointUTM, WGS_84, new LatLonPoint.Double());
            points.add(new Coordinate(pointLL.getLatitude(), pointLL.getLongitude()));
        }

        return new MapPolygonImpl(points);
    }

    private static Color colorFromState(EquipmentState state) {
        if (state == null) {
            return WHITE;
        }
        return COLOR_FROM_STATE.getOrDefault(state, WHITE);
    }
}
