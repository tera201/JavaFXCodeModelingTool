package org.tera201;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class InfoPane {
    private StackPane stackPane;
    private final AnchorPane anchorPane = new AnchorPane();
    private final Label infoLabel = new Label();

    public InfoPane() {
        StackPane.setAlignment(anchorPane, Pos.BOTTOM_RIGHT);
        AnchorPane.setBottomAnchor(infoLabel, 0.);
        AnchorPane.setRightAnchor(infoLabel, 0.);
        anchorPane.setMinSize(30, 20);
        anchorPane.setMaxSize(100, 100);
        infoLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 5;");
        anchorPane.getChildren().add(infoLabel);

    }

    public void setMainPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public StackPane getMainPane() {
        return stackPane;
    }

    public void updateInfoPane(String header) {
        infoLabel.setText(header);
    }

    public void hideInfo() {
        stackPane.getChildren().remove(anchorPane);
    }

    public void showInfo() {
        stackPane.getChildren().add(anchorPane);
    }
}
