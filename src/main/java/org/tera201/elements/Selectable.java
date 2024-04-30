package org.tera201.elements;

public interface Selectable {
    void setHighlighted(boolean highlighted);

    String getInfo();

    String getHeader();

    String getObjectPath();
    String getFilePath();
}
