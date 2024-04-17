package org.tera201.elements.city;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.tera201.SelectionManager;
import org.tera201.elements.Selectable;
import org.tera201.elements.SpaceListObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Quarter extends Box implements SpaceListObject<Building>, AddNewPosition, Selectable {
    private static final double HALF = 0.5;

    private String name;
    private String path = "";
    private final Map<String, Building> buildings = new HashMap<>();
    private City city;
    private final Point lastPoint;
    private Tooltip tooltip;

    private final Group group = new Group();

    private final List<BuildingPosition> buildingPositionList = new ArrayList<>();

    private boolean rotated;

    double separate;
    private SelectionManager selectionManager;

    public Quarter(double width, double height, double depth, double separate) {
        this(null, width, height, depth, separate);
    }

    public Quarter(String name, double width, double height, double depth, double separate) {
        super(width, height, depth);
        this.name = name;
        this.path = name;
        setTranslateY(height / 2);
        this.separate = separate;
        lastPoint = new Point(this, separate);
        group.getChildren().add(this);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.LIGHTBLUE);
        material.setSpecularColor(Color.BLACK);
        setMaterial(material);
        ensureTooltip().setText(name);
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        ((PhongMaterial) getMaterial()).setDiffuseColor(highlighted ? Color.RED : Color.LIGHTBLUE);
    }

    @Override
    public String getInfo() {
        return buildings.values().stream().map(it ->
                String.format("%s {w: %f, h: %f, d: %f} %n", it.getName(), it.getWidth(), it.getHeight(), it.getDepth()))
                .collect(Collectors.joining());
    }

    @Override
    public String getHeader() {
        return  name;
    }

    @Override
    public String getObjectPath() {
        return path;
    }

    public void isRotated(boolean rotated) {
        this.rotated = rotated;
    }

    public boolean isRotated() {
        return rotated;
    }

    public Group getGroup() { return this.group; }

    public String getName() { return this.name; }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    public City getCity() { return  this.city; }

    public void setCity(City city) {
        this.city = city;
    }

    private Tooltip ensureTooltip() {
        if(tooltip == null) {
            tooltip = new Tooltip(name);
            Tooltip.install(this, tooltip);
        }
        return tooltip;
    }

    @Override
    public void addObject(Building building) {
        buildings.put(building.getName(), building);
        group.getChildren().add(building);
        setBuildingPosition(building);
        building.setSelectionManager(selectionManager);
        building.setPath(path + ":" + building.getPath());
    }

    public void setBuildingPosition(Building building) {
        adjustBuildingPosition(building);
        updateLastPointWith(building);
        resizeByLastPosition();
    }

    private void adjustBuildingPosition(Building building) {
        if (shouldInverseBuilding(building)) {
            building.inverse();
        }
        Optional<BuildingPosition> potentialPosition = findPotentialPositionFor(building);
        if (potentialPosition.isPresent()) {
            positionBuildingBasedOnPrevious(building, potentialPosition.get());
        } else {
            positionBuildingAtStart(building);
        }
    }

    private Optional<BuildingPosition> findPotentialPositionFor(Building building) {
        return buildingPositionList.stream()
                .filter(it -> isPositionSuitableForBuilding(it, building))
                .max(Comparator.comparingDouble(it -> it.upperY));
    }

    private void resizeByLastPosition() {
        BuildingPosition lastBuildingPosition = buildingPositionList.get(buildingPositionList.size() - 1);
        if (lastBuildingPosition.upperY > this.getDepth()) this.setDepth(lastBuildingPosition.upperY);
    }

    private void resize() {
        AtomicReference<Double> sumSq = new AtomicReference<>((double) 0);
        AtomicReference<Double> maxSide = new AtomicReference<>((double) 0);
        buildings.values().forEach(building -> {
            sumSq.updateAndGet(v -> v + (building.getWidth() + this.separate) * (building.getDepth() + this.separate));
            if (maxSide.get() < (building.getWidth() + this.separate) || maxSide.get() < (building.getDepth() + this.separate)) maxSide.set(Math.max(building.getWidth(), building.getDepth()) + this.separate);
        });
        double side = Math.max(Math.sqrt(sumSq.get()), maxSide.get());
        this.setWidth(side);
        this.setDepth(side);
    }

    private boolean shouldInverseBuilding(Building building) {
        return lastPoint.needNewLine(building.getWidth()) && !lastPoint.needNewLine(building.getDepth()) ||
                        lastPoint.getReminderToBorder(building.getDepth()) < lastPoint.getReminderToBorder(building.getWidth());
    }

    private void positionBuildingBasedOnPrevious(Building building, BuildingPosition previousPosition) {
        double newDepth = previousPosition.upperY + separate;
        bindBuilding(building, newDepth);
        buildingPositionList.add(new BuildingPosition(lastPoint.getX(building.getWidth()), newDepth, building.getWidth(), building.getDepth()));
    }

    private void positionBuildingAtStart(Building building) {
        bindBuilding(building, 0);
        buildingPositionList.add(new BuildingPosition(lastPoint.getX(building.getWidth()), 0, building.getWidth(), building.getDepth()));
    }

    private void bindBuilding(Building building, double depth) {
        building.translateZProperty().bind(translateZProperty().add(depth).add(building.depthProperty().multiply(HALF)).subtract(depthProperty().multiply(HALF)));
        building.translateXProperty().bind(translateXProperty().add(lastPoint.getX(building.getWidth())).add(building.getWidth() * HALF).subtract(widthProperty().multiply(HALF)));
        building.translateYProperty().bind(translateYProperty().subtract(heightProperty().multiply(HALF)).subtract(building.heightProperty().multiply(HALF)));
    }

    private boolean isPositionSuitableForBuilding(BuildingPosition position, Building building) {
        double buildingEndX = lastPoint.getX(building.getWidth()) + building.getWidth();
        return (lastPoint.getX(building.getWidth()) <= position.leftX && buildingEndX >= position.leftX) ||
                (lastPoint.getX(building.getWidth()) <= position.rightX && buildingEndX >= position.rightX) ||
                (lastPoint.getX(building.getWidth()) >= position.leftX && buildingEndX <= position.rightX);
    }

    private void updateLastPointWith(Building building) {
        lastPoint.setX(lastPoint.getX() + building.getWidth());
        lastPoint.setMaxZ(building.getDepth());
    }

    @Override
    public void addAllObjects(Building... buildings) {
        Arrays.stream(buildings).forEach(this::addObject);
    }

    @Override
    public void removeObject(Building building) {
        buildings.remove(building.getName());
        this.group.getChildren().remove(building);
    }

    @Override
    public void removeAllObjects(Building... buildings) {
        Arrays.stream(buildings).forEach(this::removeObject);
    }

    @Override
    public void clear() {
        buildings.clear();
        this.group.getChildren().clear();
        group.getChildren().add(this);
        clearPosition();
    }

    public void updateView() {
        clearPosition();
        resize();
        buildings.values().stream().sorted(Comparator.comparingDouble(q -> - q.getWidth() * q.getDepth())).forEach(this::setBuildingPosition);
        buildings.values().forEach(it -> it.setSelectionManager(selectionManager));
    }

    public void clearPosition() {
        buildingPositionList.clear();
        lastPoint.reset();
    }

    @Override
    public void setPosition(double newX, double newZ) {
        setTranslateX(-newX);
        setTranslateZ(-newZ);
    }

    @Override
    public void setNotes(String notes) {
        ensureTooltip().setText(notes);
    }

    @Override
    public void setSelectionManager(SelectionManager selectionManager) {
        buildings.values().forEach(it -> it.setSelectionManager(selectionManager));
        this.selectionManager = selectionManager;
        this.setOnMouseClicked(event -> {
            if (selectionManager != null) {
                this.selectionManager.setSelected(this);
            }
            event.consume();  // stop event propagation
        });
    }

    public Map<String, Building> getBuildings() {
        return buildings;
    }

    private static class BuildingPosition {
        double leftX;
        double lowerY;
        double rightX;
        double upperY;
        public BuildingPosition(double leftX, double lowerY, double width, double height) {
            this.leftX = leftX;
            this.lowerY = lowerY;
            this.rightX = leftX + width;
            this.upperY = lowerY + height;
        }
    }
}
