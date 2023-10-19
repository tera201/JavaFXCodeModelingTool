package org.tera201;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
import org.tera201.elements.city.Building;
import org.tera201.elements.city.City;
import org.tera201.elements.FXSpace;
import org.tera201.elements.city.Quarter;

import java.util.Random;

public class CityMainScene extends Application {
    private static final double SCENE_WIDTH = 800;
    private static final double SCENE_HEIGHT = 600;

    Random rand = new Random();
    InfoPane infoPane = new InfoPane();

    @Override
    public void start(Stage stage) {
        City city = new City(8000, 20, 8000);
        createQuarters(120, city);
        FXSpace<Box> fxSpace = new FXSpace<>(city);
        MainSubScene mainSubScene = new MainSubScene(fxSpace, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);
        StackPane stackPane = new StackPane(mainSubScene);
        InfoPane.setMainPane(stackPane);

        mainSubScene.heightProperty().bind(stackPane.heightProperty());
        mainSubScene.widthProperty().bind(stackPane.widthProperty());

        Scene scene2 = new Scene(stackPane, SCENE_WIDTH, SCENE_HEIGHT, true, SceneAntialiasing.BALANCED);

        city.updateView();

        stage.setTitle("3D Dragging");
        stage.setScene(scene2);
        stage.show();
    }

    public void createQuarters(int n, City city) {
        for (int i = 0; i < n; i++) {
            Quarter quarter = new Quarter(i + " quarter", 500, 10, 500, 50);
            createBuildings(randomValue(), quarter);
            city.addObject(quarter);
        }
    }

    public void createBuildings(int n, Quarter quarter) {
        for (int i = 0; i < n; i++) {
            Building building1 = new Building("%d".formatted(i),200 * randomDValue(), 900 * randomDValue(), 200 * randomDValue());
            building1.setInfo("""
                Width: %s
                Height: %s
                Depth: %s""".formatted(building1.getWidth(), building1.getHeight(), building1.getDepth()));
            quarter.addObject(building1);
        }
    }

    private Double randomDValue() {
        return rand.nextDouble(0.2, 1.0);
    }

    private int randomValue() {
        return rand.nextInt(4, 13);
    }

    public static void main(String[] args) {
        launch(args);
    }
}