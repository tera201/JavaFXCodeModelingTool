package org.tera201;

import org.tera201.elements.Selectable;

public class SelectionManager {

    private InfoPane infoPane;
    public SelectionManager(InfoPane infoPane) {
        this.infoPane = infoPane;
    }
    private Selectable selected;

    public Selectable getSelected() {
        return selected;
    }

    public void setSelected(Selectable selectable) {
        if (selected != null) {
            selected.setHighlighted(false);
            infoPane.hideInfo();
        }
        selected = selectable;
        if (selected != null) {
            selected.setHighlighted(true);
            infoPane.updateInfoPane(selected.getHeader(), selected.getInfo());
            infoPane.showInfo();
        }
    }
}
