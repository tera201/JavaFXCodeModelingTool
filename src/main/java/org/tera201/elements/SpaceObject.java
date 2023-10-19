package org.tera201.elements;

import javafx.scene.control.Tooltip;

public interface SpaceObject {
    String getName();

    void setPosition(double newX, double newZ);

    Tooltip ensureTooltip();

    void setNotes(String notes);
}
