package org.tera201.elements;

import javafx.scene.Group;
import javafx.scene.Node;
import org.tera201.SelectionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FXSpace<T> extends Group {

    private SpaceListObject<T> mainObject;
    private final List<SpaceListObject<T>> mainListObjects = new ArrayList<>();
    private SelectionManager selectionManager;

    public FXSpace() {}

    public FXSpace(SpaceObject... spaceObject) {
        Arrays.stream(spaceObject).forEach(this::add);
    }

    public FXSpace(SpaceObject spaceObject) {
        add(spaceObject);
    }

    public void add(SpaceObject spaceObject) {
        if (spaceObject instanceof  SpaceListObject spaceListObject) {
            if (getChildren().isEmpty()) mainObject = spaceListObject;
            mainListObjects.add(spaceListObject);
            getChildren().add(spaceListObject.getGroup());
        }
        else
            getChildren().add((Node) spaceObject);
        spaceObject.setSelectionManager(selectionManager);
    }

    public void remove(SpaceObject spaceObject) {
        if (spaceObject instanceof  SpaceListObject packageCircle)
            getChildren().remove(packageCircle.getGroup());
        else
            getChildren().remove(spaceObject);
    }

    public void clean() {
        getChildren().clear();
        mainListObjects.forEach(it -> it.setSelectionManager(null));
        mainListObjects.clear();
        mainObject.setSelectionManager(null);
        mainObject.clear();
        mainObject = null;
    }

    public void setSelectionManager(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
        mainObject.setSelectionManager(selectionManager);
        mainListObjects.forEach(it -> it.setSelectionManager(selectionManager));
    }

    public SelectionManager getSelectionManager() {
        return  selectionManager;
    }

    public SpaceListObject<T> getMainObject() {
        return mainObject;
    }

    public List<SpaceListObject<T>> getMainListObjects() {
        return mainListObjects;
    }

    public void setMainObject(SpaceListObject<T> spaceListObject) {
        mainObject = spaceListObject;
    }
}

