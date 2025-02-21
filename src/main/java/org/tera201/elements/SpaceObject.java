package org.tera201.elements;

import javafx.scene.control.Tooltip;
import org.tera201.SelectionManager;

public interface SpaceObject {
    String getName();

    String getPath();

    String getFilePath();

    void  setPath(String path);

    void setFilePath(String filePath);

    void setPosition(double newX, double newZ);

    void setNotes(String notes);

    void setSelectionManager(SelectionManager selectionManager);
}
