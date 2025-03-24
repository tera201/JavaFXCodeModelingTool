package org.tera201;

import javafx.scene.input.MouseEvent;
import org.tera201.elements.Selectable;
import org.tera201.elements.SelectionObserver;

import java.util.ArrayList;
import java.util.List;

public class SelectionManager {

    private final InfoPane infoPane;
    private Selectable selected;
    private final List<SelectionObserver> observers = new ArrayList<>();
    public SelectionManager(InfoPane infoPane) {
        this.infoPane = infoPane;
    }

    public void setSelected(Selectable selectable, MouseEvent event) {
        if (selected != null) {
            selected.setHighlighted(false);
            infoPane.hideInfo();
        }
        selected = selectable;
        if (selected != null) {
            selected.setHighlighted(true);
            infoPane.updateInfoPane(selected.getHeader());
            infoPane.showInfo();
        }
        notifyObservers();
    }

    public Selectable getSelected() {
        return selected;
    }

    public void addObserver(SelectionObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(SelectionObserver observer) {
        observers.remove(observer);
    }

    public void cleanObserver() {
        observers.clear();
    }

    private void notifyObservers() {
        for (SelectionObserver observer : observers) {
            observer.onSelectionChanged(selected);
        }
    }
}
