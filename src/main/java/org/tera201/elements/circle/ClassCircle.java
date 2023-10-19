package org.tera201.elements.circle;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.tera201.SelectionManager;
import org.tera201.elements.Selectable;
import org.tera201.elements.SpaceObject;

public class ClassCircle extends HollowCylinder implements SpaceObject, Selectable {
    private final String name;
    private Tooltip tooltip;
    private final Color defaultColor = Color.LIGHTBLUE;
    private SelectionManager selectionManager;

    public ClassCircle(String name, double radiusOuter, double radiusInner, double height) {
        super(radiusOuter, radiusInner, height);
        this.name = name;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(defaultColor);
        material.setSpecularColor(Color.BLACK);
        setMaterial(material);
        this.setOnMouseClicked(event -> {
            selectionManager.setSelected(this);
            event.consume();  // stop event propagation
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setPosition(double newX, double newZ) {
        setTranslateX(newX);
        setTranslateZ(newZ);
    }

    private Tooltip ensureTooltip() {
        if(tooltip == null) {
            tooltip = new Tooltip(name);
            Tooltip.install(this, tooltip);
        }
        return tooltip;
    }

    @Override
    public void setNotes(String notes) {
        ensureTooltip().setText(notes);
    }

    @Override
    public void setSelectionManager(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        ((PhongMaterial) getMaterial()).setDiffuseColor(highlighted ? Color.RED : defaultColor);
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public String getHeader() {
        return name;
    }
}
