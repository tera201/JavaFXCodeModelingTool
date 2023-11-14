package org.tera201;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.tera201.elements.FXSpace;
import org.tera201.elements.circle.ClassCircle;
import org.tera201.elements.circle.HollowCylinder;
import org.tera201.elements.circle.PackageCircle;

public class CircleMainScene extends Application {
    private static final double SCENE_WIDTH = 800;
    private static final double SCENE_HEIGHT = 600;


    @Override
    public void start(Stage stage) {
        PackageCircle packageCircle = new PackageCircle("1Pack", 1800d, 1673.2421875, 100d);
        ClassCircle circle = new ClassCircle("11", 800, 400, 100);
        ClassCircle circle2 = new ClassCircle("12", 3000, 600, 100);
        ClassCircle circle3 = new ClassCircle("13", 800, 400, 100);
        ClassCircle circle4 = new ClassCircle("14", 900, 400, 100);
        ClassCircle circle5 = new ClassCircle("15", 500, 400, 100);
        ClassCircle circle6 = new ClassCircle("16", 400, 300, 100);
        ClassCircle circle7 = new ClassCircle("17", 300, 200, 100);
        packageCircle.addAllObjects(circle, circle2, circle3, circle4, circle5, circle6, circle7);

        FXSpace<HollowCylinder> fxSpace = new FXSpace<>(packageCircle);

        MainSubScene mainSubScene = new MainSubScene(fxSpace, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        StackPane stackPane = new StackPane(mainSubScene);
        fxSpace.updateView();

        InfoPane infoPane = new InfoPane();
        infoPane.setMainPane(stackPane);
        System.out.println(packageCircle.getOuterRadius() * 2);
        SelectionManager selectionManager = new SelectionManager(infoPane);
        fxSpace.setSelectionManager(selectionManager);

        Scene scene2 = new Scene(stackPane, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        stage.setTitle("3D Dragging");
        stage.setScene(scene2);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}