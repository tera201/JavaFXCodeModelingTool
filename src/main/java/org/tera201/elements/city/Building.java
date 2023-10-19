package org.tera201.elements.city;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.tera201.SelectionManager;
import org.tera201.elements.Selectable;
import org.tera201.elements.SpaceObject;

public class Building extends Box implements SpaceObject, Selectable {
    private final String name;
    private Tooltip tooltip;
    private String info;
    public Building(double width, double height, double depth) {
        this(null, width, height, depth);
    }
    public Building(String name, double width, double height, double depth) {
        super(width, height, depth);
        this.name = name;
        setTranslateY(-height / 2);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.PURPLE);
        material.setSpecularColor(Color.BLACK);
        setMaterial(material);
        ensureTooltip().setText(name);
        this.setOnMouseClicked(event -> {
            SelectionManager.setSelected(this);
            event.consume();  // stop event propagation
        });
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        ((PhongMaterial) getMaterial()).setDiffuseColor(highlighted ? Color.RED : Color.PURPLE);
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public String getHeader() {
        return name;
    }

    private Tooltip ensureTooltip() {
        if(tooltip == null) {
            tooltip = new Tooltip(name);
            Tooltip.install(this, tooltip);
        }
        return tooltip;
    }

    public void inverse() {
        double width = getWidth();
        setWidth(getDepth());
        setDepth(width);
    }

    public String getName() { return name; }

    @Override
    public void setPosition(double newX, double newZ) {
        setTranslateX(-newX);
        setTranslateZ(-newZ);
    }

    public void setNotes(String notes) {
        ensureTooltip().setText(notes);
    }
}
