package org.tera201.elements;

import javafx.scene.Group;

public interface SpaceListObject<T> extends SpaceObject {

    Group getGroup();

    void addObject(T object);

    SpaceObject findObjectByPath(String path);

    void addAllObjects(T... objects);

    void removeObject(T object);

    void removeAllObjects(T... objects);

    void clear();

    void updateView();

    void updateView(boolean experimental);
}
