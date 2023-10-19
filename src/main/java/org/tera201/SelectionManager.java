package org.tera201;

import org.tera201.elements.Selectable;

public class SelectionManager {
    private SelectionManager() {}
    private static Selectable selected;

    public static Selectable getSelected() {
        return selected;
    }

    public static void setSelected(Selectable selectable) {
        if (selected != null) {
            selected.setHighlighted(false);
            InfoPane.hideInfo();
        }
        selected = selectable;
        if (selected != null) {
            selected.setHighlighted(true);
            InfoPane.updateInfoPane(selected.getHeader(), selected.getInfo());
            InfoPane.showInfo();
        }
    }
}
