package org.tera201;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class InfoPane {
    private StackPane stackPane;
    private final AnchorPane anchorPane = new AnchorPane();
    private final TitledPane infoPane = new TitledPane();
    private double initialX;
    private double initialY;
    private double initialWidth;
    private double initialHeight;

    public InfoPane() {
        StackPane.setAlignment(anchorPane, Pos.BOTTOM_RIGHT);
        infoPane.setCollapsible(false);
        anchorPane.setPrefSize(100, 100);
        anchorPane.setMaxSize(100, 100);
        anchorPane.getChildren().add(infoPane);

        Region resizeCorner = new Region();
        resizeCorner.setStyle("-fx-background-color: black;");
        resizeCorner.setPrefSize(10, 10);
        resizeCorner.setMaxSize(10,10);
        resizeCorner.setOnMousePressed(this::handleMousePressed);
        resizeCorner.setOnMouseDragged(this::handleMouseDragged);
        resizeCorner.setOnMouseEntered(event -> resizeCorner.setCursor(Cursor.NW_RESIZE));
        resizeCorner.setOnMouseExited(event -> resizeCorner.setCursor(Cursor.DEFAULT));
        anchorPane.getChildren().add(resizeCorner);
        AnchorPane.setTopAnchor(resizeCorner, 0.0);
        AnchorPane.setLeftAnchor(resizeCorner, 0.0);

    }

    public void setMainPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public StackPane getMainPane() {
        return stackPane;
    }

    public void updateInfoPane(String header, String info) {
        Label infoLabel = new Label(info);
        VBox vbox = new VBox(infoLabel);
        vbox.setAlignment(Pos.TOP_LEFT);
        infoPane.setContent(vbox);
        infoPane.setPrefSize(100, 100);
        infoPane.setText(header + " Information");
    }

    public void hideInfo() {
        stackPane.getChildren().remove(anchorPane);
    }

    public void showInfo() {
        stackPane.getChildren().add(anchorPane);
    }

    private void handleMousePressed(MouseEvent event) {
        initialX = event.getSceneX();
        initialY = event.getSceneY();
        initialWidth = infoPane.getWidth();
        initialHeight = infoPane.getHeight();
    }

    private void handleMouseDragged(MouseEvent event) {
        double deltaX = - event.getSceneX() + initialX;
        double deltaY = - event.getSceneY() + initialY;
        if (initialWidth + deltaX > 100 && initialWidth + deltaX < stackPane.getWidth())
            infoPane.setPrefWidth(initialWidth + deltaX);
        if (initialHeight + deltaY > 100 && initialHeight + deltaY < stackPane.getHeight())
            infoPane.setPrefHeight(initialHeight + deltaY);
    }
}
