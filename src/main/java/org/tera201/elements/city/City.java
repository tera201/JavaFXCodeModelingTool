package org.tera201.elements.city;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import org.tera201.SelectionManager;
import org.tera201.elements.SpaceListObject;
import org.tera201.elements.SpaceObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class City extends Box implements SpaceListObject<Quarter>, AddNewPosition {
    private static final double HALF = 0.5;
    private static final double SEPARATE = 50.0;
    private final Point lastPoint;
    private final String name;
    private String path;
    private final Group group = new Group();
    private final List<QuarterPosition> quarterPositionList =  new ArrayList<>();
    private final Map<String,Quarter> quarters = new HashMap<>();
    private SelectionManager selectionManager;

    public City(double width, double height, double depth) {
        this(width, height, depth, "");
    }
    public City(double width, double height, double depth, String name) {
        super(width, height, depth);
        this.name = name;
        this.path = name;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.GRAY);
        material.setSpecularColor(Color.BLACK);
        this.setMaterial(material);
        this.setTranslateY(height);
        lastPoint = new Point(this, 50);
        group.getChildren().add(this);
    }

    @Override
    public void addObject(Quarter quarter) {
        this.quarters.put(quarter.getName(), quarter);
        quarter.setPath(name + ":" + quarter.getPath());
        this.group.getChildren().add(quarter.getGroup());
        setQuarterPosition(quarter);
        quarter.setSelectionManager(selectionManager);
    }

    @Override
    public SpaceObject findObjectByPath(String path) {
        return quarters.values().stream().map(quarter -> {
            if (quarter.getPath().equals(path)) {
                return quarter;
            } else if (path.startsWith(quarter.getPath())) {
                return quarter.findObjectByPath(path);
            } else return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public void setQuarterPosition(Quarter quarter) {
        adjustQuarterPosition(quarter);
        updateLastPointWith(quarter);
    }

    private void adjustQuarterPosition(Quarter quarter) {
        quarter.isRotated(shouldInverseQuarter(quarter));
        Optional<QuarterPosition> potentialPosition = findPotentialPositionFor(quarter);
        if (potentialPosition.isPresent()) {
            positionQuarterBasedOnPrevious(quarter, potentialPosition.get());
        } else {
            positionQuarterAtStart(quarter);
        }
    }

    private Optional<QuarterPosition> findPotentialPositionFor(Quarter quarter) {
        return quarterPositionList.stream()
                .filter(it -> isPositionSuitableForQuarter(it, quarter))
                .max(Comparator.comparingDouble(it -> it.upperY));
    }

    private void resizeByLastPosition() {
        if (lastPoint.getMaxZ() > this.getDepth()) this.setDepth(lastPoint.getMaxZ());
    }

    private void resize() {
        AtomicReference<Double> sumSq = new AtomicReference<>((double) 0);
        quarters.values().forEach(quarter ->
            sumSq.updateAndGet(v -> v + (quarter.getWidth() + SEPARATE) * (quarter.getDepth() + SEPARATE)));
        this.setWidth(Math.sqrt(sumSq.get()));
        this.setDepth(Math.sqrt(sumSq.get()));
    }

    private boolean shouldInverseQuarter(Quarter quarter) {
        return lastPoint.needNewLine(quarter.getWidth()) && !lastPoint.needNewLine(quarter.getDepth()) ||
                        lastPoint.getReminderToBorder(quarter.getDepth()) < lastPoint.getReminderToBorder(quarter.getWidth());
    }

    private void positionQuarterBasedOnPrevious(Quarter quarter, QuarterPosition previousPosition) {
        double newHeight = previousPosition.upperY + SEPARATE;
        double width = quarter.isRotated() ? quarter.getDepth() : quarter.getWidth();
        double depth = quarter.isRotated() ? quarter.getWidth() : quarter.getDepth();
        bindQuarter(quarter, newHeight);
        quarterPositionList.add(new QuarterPosition(lastPoint.getX(width), newHeight, width, depth));
    }

    private void positionQuarterAtStart(Quarter quarter) {
        bindQuarter(quarter, 0);
        double width = quarter.isRotated() ? quarter.getDepth() : quarter.getWidth();
        double depth = quarter.isRotated() ? quarter.getWidth() : quarter.getDepth();
        quarterPositionList.add(new QuarterPosition(lastPoint.getX(width), 0, width, depth));
    }

    private void bindQuarter(Quarter quarter, double depth) {
        quarter.getGroup().getTransforms().clear();
        if (quarter.isRotated()) {
            Rotate rotateQ = new Rotate(90, Rotate.Y_AXIS);
            rotateQ.setPivotX(-quarter.getWidth() / 2);
            rotateQ.setPivotZ(-quarter.getDepth() / 2);
            quarter.getGroup().getTransforms().add(rotateQ);
        }
        quarter.getGroup().translateZProperty().bind(this.translateZProperty().add(depth).add(quarter.depthProperty().multiply(HALF)).subtract(this.depthProperty().multiply(HALF)).add(quarter.isRotated()?quarter.getWidth():0));
        quarter.getGroup().translateXProperty().bind(this.translateXProperty().add(lastPoint.getX(quarter.getWidth())).add(quarter.getWidth() * HALF).subtract(this.widthProperty().multiply(HALF)));

    }

    private boolean isPositionSuitableForQuarter(QuarterPosition position, Quarter quarter) {
        double width = quarter.isRotated() ? quarter.getDepth() : quarter.getWidth();
        double buildingEndX = lastPoint.getX(width) + width;
        return (lastPoint.getX(width) <= position.leftX && buildingEndX >= position.leftX) ||
                (lastPoint.getX(width) <= position.rightX && buildingEndX >= position.rightX) ||
                (lastPoint.getX(width) >= position.leftX && buildingEndX <= position.rightX);
    }

    private void updateLastPointWith(Quarter quarter) {
        double width = quarter.isRotated() ? quarter.getDepth() : quarter.getWidth();
        lastPoint.setX(lastPoint.getX() + width);
        lastPoint.setMaxZ(quarterPositionList.get(quarterPositionList.size() - 1).upperY);
    }

    public Quarter getQuarter(String quarterName) {
        return this.quarters.get(quarterName);
    }

    @Override
    public void addAllObjects(Quarter... quarters) {
        Arrays.stream(quarters).forEach(this::addObject);
    }

    @Override
    public void removeObject(Quarter quarter) {
        this.group.getChildren().remove(quarter.getGroup());
        quarters.remove(quarter.getName());
        quarterPositionList.remove(quarter);
        updateView();
    }

    @Override
    public void removeAllObjects(Quarter... quarters) {
        Arrays.stream(quarters).forEach(this::removeObject);
    }

    @Override
    public Group getGroup() {
        return group;
    }

    public void clear() {
        group.getChildren().clear();
        quarters.clear();
        group.getChildren().add(this);
        clearPosition();
    }

    public void clearPosition() {
        quarterPositionList.clear();
        lastPoint.reset();
    }

    public void updateView() {
        clearPosition();
        quarters.values().stream().forEach(Quarter::updateView);
        resize();
        quarters.values().stream().sorted(Comparator.comparingDouble(q -> - q.getWidth() * q.getDepth())).forEach(quarter -> {
            setQuarterPosition(quarter);
        });
        quarters.values().forEach(it -> it.setSelectionManager(selectionManager));
        resizeByLastPosition();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setPosition(double newX, double newZ) {
    }

    @Override
    public void setNotes(String notes) {
    }

    @Override
    public void setSelectionManager(SelectionManager selectionManager) {
        quarters.values().forEach(it -> it.setSelectionManager(selectionManager));
        this.selectionManager = selectionManager;
    }

    private static class QuarterPosition {
        double leftX;
        double rightX;
        double lowerY;
        double upperY;
        public QuarterPosition(double leftX, double lowerY, double width, double height) {
            this.leftX = leftX;
            this.lowerY = lowerY;
            this.rightX = leftX + width;
            this.upperY = lowerY + height;
        }
    }
}
