package org.tera201.elements.circle;

import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.tera201.SelectionManager;
import org.tera201.elements.Selectable;
import org.tera201.elements.SelectionObserver;
import org.tera201.elements.SpaceObject;

import java.util.concurrent.atomic.AtomicLong;

public class ClassCircle extends HollowCylinder implements SpaceObject, Selectable, SelectionObserver {
    private final String name;
    private String path;
    private Tooltip tooltip;
    private final Color defaultColor = Color.LIGHTBLUE;
    private SelectionManager selectionManager;

    public ClassCircle(String name, double radiusOuter, double radiusInner, double height) {
        super(radiusOuter, radiusInner, height);
        this.name = name;
        this.path = name;
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(defaultColor);
        material.setSpecularColor(Color.BLACK);
        setMaterial(material);
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
        AtomicLong mousePressTime = new AtomicLong();
        if (selectionManager != null)
            selectionManager.addObserver(this);

        this.setOnMousePressed(event -> mousePressTime.set(System.currentTimeMillis()));
        this.setOnMouseReleased(event -> mousePressTime.set(System.currentTimeMillis() - mousePressTime.get()));
        this.setOnMouseClicked(event -> {
            if (selectionManager != null && mousePressTime.get() < 200) {
                this.selectionManager.setSelected(this);
            }
            event.consume();  // stop event propagation
        });
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

    @Override
    public void onSelectionChanged(Selectable newSelection) {
        if (this.equals(newSelection) || newSelection == null ) {
            this.setVisible(true);
        } else {
            String selectedPath = ((SpaceObject)newSelection).getPath();
            if (getFirstPathNode(selectedPath).equals(getFirstPathNode(path)) ||
                    getPathWithoutFirstNode(selectedPath).equals(getPathWithoutFirstNode(path)))
                this.setVisible(true);
            else this.setVisible(false);
        }
    }

    private String getFirstPathNode(String path) {
        int dotIndex = path.indexOf(':');
        if (dotIndex != -1) {
            return path.substring(0, dotIndex + 1);
        }
        return path;
    }

    private String getPathWithoutFirstNode(String path) {
        int dotIndex = path.lastIndexOf(':');
        if (dotIndex != -1) {
            return path.substring(dotIndex, path.length() - 1);
        }
        return path;
    }
}
