package com.github.vkremianskii.pits.frontends.ui;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.proj.coords.UTMPoint;
import com.github.vkremianskii.pits.frontends.logic.MainViewPresenter;
import com.github.vkremianskii.pits.registry.types.model.Equipment;
import com.github.vkremianskii.pits.registry.types.model.EquipmentState;
import com.github.vkremianskii.pits.registry.types.model.EquipmentType;
import com.github.vkremianskii.pits.registry.types.model.Position;
import com.github.vkremianskii.pits.registry.types.model.equipment.Shovel;
import com.github.vkremianskii.pits.registry.types.model.equipment.Truck;
import com.github.vkremianskii.pits.registry.types.model.equipment.TruckState;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;

import static com.bbn.openmap.proj.Ellipsoid.WGS_84;
import static com.bbn.openmap.proj.coords.UTMPoint.LLtoUTM;
import static com.bbn.openmap.proj.coords.UTMPoint.UTMtoLL;
import static com.github.vkremianskii.pits.registry.types.model.EquipmentType.TRUCK;
import static java.awt.Color.WHITE;
import static java.util.Objects.requireNonNull;
import static javax.swing.BorderFactory.createCompoundBorder;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.Box.createRigidArea;
import static javax.swing.BoxLayout.X_AXIS;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class MainViewImpl implements MainView {

    private static final Logger LOG = LoggerFactory.getLogger(MainViewImpl.class);
    private static final int DEFAULT_MAP_CENTER_X = 643928;
    private static final int DEFAULT_MAP_CENTER_Y = 270764;
    private static final int DEFAULT_MAP_ZOOM = 12;
    private static final double DEFAULT_LATITIUDE = 65.291190;
    private static final double DEFAULT_LONGITUDE = 41.035438;
    private static final int DEFAULT_ELEVATION = 0;
    private static final int DEFAULT_PAYLOAD = 0;
    private static final int NUM_CIRCLE_SEGMENTS = 32;
    private static final String OSM_USER_AGENT = "pits.frontends/1.0-SNAPSHOT";

    private static final Map<EquipmentState, Color> COLOR_FROM_STATE = Map.of(
        TruckState.EMPTY, new Color(0x90ee90),
        TruckState.WAIT_LOAD, new Color(0xadd8e6),
        TruckState.LOAD, new Color(0x00008b),
        TruckState.HAUL, new Color(0x006400),
        TruckState.UNLOAD, new Color(0x00008b));

    private final MainViewPresenter presenter;
    private final DefaultListModel<EquipmentListElement> fleetListModel = new DefaultListModel<>();

    private JList<EquipmentListElement> fleetListBox;
    private JButton fleetInitializeButton;
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

    public MainViewImpl(MainViewPresenter presenter) {
        this.presenter = requireNonNull(presenter);
    }

    public void initialize() {
        final var fleetPanel = bootstrapFleetPanel();
        fleetPanel.setPreferredSize(new Dimension(175, fleetPanel.getPreferredSize().height));

        final var equipmentPanel = bootstrapEquipmentPanel();
        equipmentPanel.setPreferredSize(new Dimension(175, equipmentPanel.getPreferredSize().height));

        final var fleetFrame = new JPanel();
        final var fleetFrameLayout = new BoxLayout(fleetFrame, X_AXIS);
        fleetFrame.setLayout(fleetFrameLayout);
        fleetFrame.setBorder(createCompoundBorder(createTitledBorder("Fleet"), createEmptyBorder(3, 3, 3, 3)));
        fleetFrame.add(fleetPanel);
        fleetFrame.add(equipmentPanel);

        final var mapPanel = bootstrapMapPanel();

        final var mainPanel = new JPanel();
        final var mainLayout = new BoxLayout(mainPanel, X_AXIS);
        mainPanel.setLayout(mainLayout);
        mainPanel.add(fleetFrame);
        mainPanel.add(mapPanel);

        final var frame = new JFrame("Frontends");
        frame.add(mainPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                presenter.onWindowClosing();
            }
        });

        fleetListBox.addListSelectionListener(ignored -> {
            final var element = fleetListBox.getSelectedValue();
            presenter.onEquipmentSelected(element.id);
        });

        sendPositionButton.addActionListener(e -> presenter.sendEquipmentPosition(
            fleetListBox.getSelectedValue().id,
            (double) latitudeSpinner.getValue(),
            (double) longitudeSpinner.getValue(),
            (int) elevationSpinner.getValue()));

        sendPayloadButton.addActionListener(e -> presenter.sendEquipmentPayload(
            fleetListBox.getSelectedValue().id,
            (int) payloadSpinner.getValue()));

        mapViewer.setDisplayPosition(
            DEFAULT_MAP_CENTER_X,
            DEFAULT_MAP_CENTER_Y,
            DEFAULT_MAP_ZOOM);
    }

    private JPanel bootstrapFleetPanel() {
        fleetListBox = new JList<>(fleetListModel);
        fleetListBox.setSelectionMode(SINGLE_SELECTION);

        fleetInitializeButton = new JButton("Initialize");
        fleetInitializeButton.setEnabled(false);
        fleetInitializeButton.addActionListener(ignored -> presenter.initializeFleet());

        final var fleetPanel = new JPanel(new BorderLayout());
        fleetPanel.setBorder(createEmptyBorder(3, 3, 3, 3));
        fleetPanel.add(new JScrollPane(fleetListBox), BorderLayout.CENTER);
        fleetPanel.add(fleetInitializeButton, BorderLayout.PAGE_END);

        return fleetPanel;
    }

    private JPanel bootstrapEquipmentPanel() {
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
        equipmentPanel.add(autoWidthComponent(nameLabel));
        equipmentPanel.add(nameTextField);
        equipmentPanel.add(createRigidArea(new Dimension(0, 3)));
        equipmentPanel.add(autoWidthComponent(typeLabel));
        equipmentPanel.add(typeTextField);
        equipmentPanel.add(createRigidArea(new Dimension(0, 3)));
        equipmentPanel.add(autoWidthComponent(stateLabel));
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
        simulationPanel.add(autoWidthComponent(latitudeLabel));
        simulationPanel.add(latitudeSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(autoWidthComponent(longitudeLabel));
        simulationPanel.add(longitudeSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(autoWidthComponent(elevationLabel));
        simulationPanel.add(elevationSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(autoWidthComponent(payloadLabel));
        simulationPanel.add(payloadSpinner);
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(autoWidthComponent(sendPositionButton));
        simulationPanel.add(createRigidArea(new Dimension(0, 3)));
        simulationPanel.add(autoWidthComponent(sendPayloadButton));

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
                LOG.debug(String.format("Map mouse click: lat=%.06f lon=%.06f", position.getLat(), position.getLon()));
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

    private static Component autoWidthComponent(Component component) {
        final var panel = new JPanel(new BorderLayout());
        panel.add(component);
        return panel;
    }

    private static Optional<MapMarkerDot> mapMarkerFromEquipment(Equipment equipment) {
        return Optional.ofNullable(equipment.position)
            .map(position -> {
                final var marker = new MapMarkerDot(position.latitude(), position.longitude());
                marker.setName(equipment.name);
                marker.setBackColor(colorFromState(equipment.state));
                return marker;
            });
    }

    private static Optional<MapPolygon> loadZoneFromShovel(Shovel shovel) {
        return Optional.ofNullable(shovel.position)
            .map(position -> newMapPolygon(position.latitude(), position.longitude(), shovel.loadRadius));
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

    @Override
    public void refreshFleetControls(SortedMap<Integer, Equipment> equipmentById) {
        final var selectedEquipment = fleetListBox.getSelectedValue();
        fleetListModel.clear();

        mapViewer.removeAllMapMarkers();
        mapViewer.removeAllMapPolygons();

        for (final var equipment : equipmentById.values()) {
            fleetListModel.addElement(new EquipmentListElement(equipment.id, equipment.name));
            mapMarkerFromEquipment(equipment).ifPresent(mapViewer::addMapMarker);
            if (equipment.type == EquipmentType.SHOVEL) {
                loadZoneFromShovel((Shovel) equipment).ifPresent(mapViewer::addMapPolygon);
            }
        }

        if (selectedEquipment != null && equipmentById.containsKey(selectedEquipment.id)) {
            fleetListBox.setSelectedValue(selectedEquipment, true);
        }
    }

    @Override
    public void refreshEquipmentControls(Equipment equipment) {
        final var position = Optional.ofNullable(equipment.position);
        final var state = Optional.ofNullable(equipment.state);

        int payload = DEFAULT_PAYLOAD;
        if (equipment.type == TRUCK) {
            final var truck = (Truck) equipment;
            if (truck.payload != null) {
                payload = truck.payload;
            }
        }

        nameTextField.setText(equipment.name);
        typeTextField.setText(equipment.type.name());
        stateTextField.setText(state.map(s -> s.name).orElse(""));
        latitudeSpinner.setValue(position.map(Position::latitude).orElse(DEFAULT_LATITIUDE));
        latitudeSpinner.setEnabled(true);
        longitudeSpinner.setValue(position.map(Position::longitude).orElse(DEFAULT_LONGITUDE));
        longitudeSpinner.setEnabled(true);
        elevationSpinner.setValue(position.map(Position::elevation).orElse(DEFAULT_ELEVATION));
        elevationSpinner.setEnabled(true);
        payloadSpinner.setValue(payload);
        payloadSpinner.setEnabled(equipment.type == TRUCK);
        sendPositionButton.setEnabled(true);
        sendPayloadButton.setEnabled(equipment.type == TRUCK);
    }

    @Override
    public void setInitializeFleetEnabled(boolean enabled) {
        fleetInitializeButton.setEnabled(enabled);
    }

    private static class EquipmentListElement {

        public final int id;
        public final String name;

        public EquipmentListElement(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
