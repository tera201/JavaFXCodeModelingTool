package org.tera201.elements;

import javafx.scene.control.Tooltip;
import org.tera201.SelectionManager;

public interface SpaceObject {
    String getName();

    void setPosition(double newX, double newZ);

    void setNotes(String notes);

    void setSelectionManager(SelectionManager selectionManager);
}
