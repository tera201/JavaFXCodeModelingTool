package org.tera201;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.tera201.elements.FXSpace;
import org.tera201.elements.circle.ClassSpace;
import org.tera201.elements.circle.PackageSpace;

public class MainScene extends Application {
    private static final double SCENE_WIDTH = 800;
    private static final double SCENE_HEIGHT = 600;
    InfoPane infoPane = new InfoPane();

    @Override
    public void start(Stage stage) {
        PackageSpace packageCircle = new PackageSpace("1Pack", 1800d, 1673.2421875, 100d);
        ClassSpace circle = new ClassSpace("11", 800, 400, 100);
        ClassSpace circle2 = new ClassSpace("12", 3000, 600, 100);
        ClassSpace circle3 = new ClassSpace("13", 800, 400, 100);
        ClassSpace circle4 = new ClassSpace("14", 900, 400, 100);
        ClassSpace circle5 = new ClassSpace("15", 500, 400, 100);
        ClassSpace circle6 = new ClassSpace("16", 400, 300, 100);
        ClassSpace circle7 = new ClassSpace("17", 300, 200, 100);
        packageCircle.addAllObjects(circle, circle2, circle3, circle4, circle5, circle6, circle7);


//        PackageCircle packageCircle2 = new PackageCircle("2Pack", 600d, 500d, 100d);
//        ClassCircle circle21 = new ClassCircle("21", 300, 200, 100);
//        packageCircle2.addCircle(circle21);
//        packageCircle.addCircle(packageCircle2);
//
//
//        PackageCircle packageCircle3 = new PackageCircle("3Pack", 16000d, 15000d, 100d);
//        PackageCircle packageCircle4 = new PackageCircle("4Pack", 600d, 500d, 100d);
//        ClassCircle circle41 = new ClassCircle("41", 500, 400, 100);
//        ClassCircle circle42 = new ClassCircle("42", 800, 400, 100);
//        packageCircle4.addAllCircles(circle41, circle42);
//        ClassCircle circle31 = new ClassCircle("31", 500, 400, 100);
//        packageCircle3.addCircle(packageCircle4);
//        packageCircle3.addCircle(circle31);
//
//        packageCircle.addCircle(packageCircle3);
        System.out.println("Update");
        packageCircle.updateView();

        FXSpace<PackageSpace> fxSpace = new FXSpace<>(packageCircle);

        MainSubScene mainSubScene = new MainSubScene(fxSpace, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        StackPane stackPane = new StackPane(mainSubScene);
        InfoPane.setMainPane(stackPane);

        Scene scene2 = new Scene(stackPane, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        stage.setTitle("3D Dragging");
        stage.setScene(scene2);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}