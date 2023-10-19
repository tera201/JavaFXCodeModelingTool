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
    private static StackPane stackPane;
    private static final AnchorPane ANCHOR_PANE = new AnchorPane();
    private static final TitledPane INFO_PANE = new TitledPane();
    private double initialX;
    private double initialY;
    private double initialWidth;
    private double initialHeight;

    public InfoPane() {
        StackPane.setAlignment(ANCHOR_PANE, Pos.BOTTOM_RIGHT);
        INFO_PANE.setCollapsible(false);
        ANCHOR_PANE.setPrefSize(100, 100);
        ANCHOR_PANE.setMaxSize(100, 100);
        ANCHOR_PANE.getChildren().add(INFO_PANE);

        Region resizeCorner = new Region();
        resizeCorner.setStyle("-fx-background-color: black;");
        resizeCorner.setPrefSize(10, 10);
        resizeCorner.setMaxSize(10,10);
        resizeCorner.setOnMousePressed(this::handleMousePressed);
        resizeCorner.setOnMouseDragged(this::handleMouseDragged);
        resizeCorner.setOnMouseEntered(event -> resizeCorner.setCursor(Cursor.NW_RESIZE));
        resizeCorner.setOnMouseExited(event -> resizeCorner.setCursor(Cursor.DEFAULT));
        ANCHOR_PANE.getChildren().add(resizeCorner);
        AnchorPane.setTopAnchor(resizeCorner, 0.0);
        AnchorPane.setLeftAnchor(resizeCorner, 0.0);

    }

    public static void setMainPane(StackPane stackPane) {
        InfoPane.stackPane = stackPane;
    }

    public static StackPane getMainPane() {
        return InfoPane.stackPane;
    }

    public static void updateInfoPane(String header, String info) {
        Label infoLabel = new Label(info);
        VBox vbox = new VBox(infoLabel);
        vbox.setAlignment(Pos.TOP_LEFT);
        INFO_PANE.setContent(vbox);
        INFO_PANE.setPrefSize(100, 100);
        INFO_PANE.setText(header + " Information");
    }

    public static void hideInfo() {
        stackPane.getChildren().remove(ANCHOR_PANE);
    }

    public static void showInfo() {
        stackPane.getChildren().add(ANCHOR_PANE);
    }

    private void handleMousePressed(MouseEvent event) {
        initialX = event.getSceneX();
        initialY = event.getSceneY();
        initialWidth = INFO_PANE.getWidth();
        initialHeight = INFO_PANE.getHeight();
    }

    private void handleMouseDragged(MouseEvent event) {
        double deltaX = - event.getSceneX() + initialX;
        double deltaY = - event.getSceneY() + initialY;
        if (initialWidth + deltaX > 100 && initialWidth + deltaX < stackPane.getWidth())
            INFO_PANE.setPrefWidth(initialWidth + deltaX);
        if (initialHeight + deltaY > 100 && initialHeight + deltaY < stackPane.getHeight())
            INFO_PANE.setPrefHeight(initialHeight + deltaY);
    }
}
