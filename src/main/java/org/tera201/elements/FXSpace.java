package org.tera201.elements;

import javafx.scene.Group;
import javafx.scene.Node;

import java.util.Arrays;

public class FXSpace<T> extends Group {

    private SpaceListObject<T> mainObject;

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
            getChildren().add(spaceListObject.getGroup());
        }
        else
            getChildren().add((Node) spaceObject);
    }

    public void remove(SpaceObject spaceObject) {
        if (spaceObject instanceof  SpaceListObject packageCircle)
            getChildren().remove(packageCircle.getGroup());
        else
            getChildren().remove(spaceObject);
    }

    public void clean() {
        getChildren().clear();
        mainObject.clear();
        mainObject = null;
    }

    public SpaceListObject<T> getMainObject() {
        return mainObject;
    }

    public void setMainObject(SpaceListObject<T> spaceListObject) {
        mainObject = spaceListObject;
    }
}

