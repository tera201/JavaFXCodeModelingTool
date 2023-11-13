package org.tera201;

import javafx.beans.NamedArg;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import org.tera201.elements.FXSpace;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MainSubScene extends SubScene {
    private final PerspectiveCamera camera;
    private int scrollSpeed = 5;
    private boolean isDynamicScrollSpeed = false;
    private double mousePosX;
    private double mousePosY;
    private double mouseOldX;
    private double mouseOldY;
    private final Rotate rotateX = new Rotate(-90, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    private final Translate camPosition = new Translate(0, 0, -10000);

    private volatile boolean spaceIsPressed=false;

    private volatile boolean isPicking=false;
    private Point3D vecIni;
    private Point3D vecPos;
    private double distance;
    private Shape3D s;
    private Instant scrollTime = Instant.now();
    private Integer scrollNum = 0;
    public MainSubScene(@NamedArg("root") FXSpace root, @NamedArg("width") double width, @NamedArg("height") double height,
                        @NamedArg("depthBuffer") boolean depthBuffer, @NamedArg("antiAliasing") SceneAntialiasing antiAliasing) {
        super(root, width, height, depthBuffer, antiAliasing);

//        this.setFill(Color.web("3d3d3d"));
        this.setFill(Color.SILVER);

        camera = new PerspectiveCamera(false);
        camera.setVerticalFieldOfView(false);

        camera.setNearClip(0.1);
        camera.setFarClip(100000.0);
        camera.getTransforms().addAll(rotateX, rotateY, camPosition);

        PointLight light = new PointLight(Color.GAINSBORO);
//        light.getTransforms().add(new Translate(0, 0, -1000));
        light.setLightOn(true);
        light.getTransforms().addAll(rotateX, rotateY, camPosition);
//        light.getTransforms().addAll(rotateX, rotateY, camPosition.clone());

//        AmbientLight ambientLight = new AmbientLight(Color.DARKGRAY);
        AmbientLight ambientLight = new AmbientLight(Color.GAINSBORO);
        ambientLight.setLightOn(true);

        AtomicLong mousePressTime = new AtomicLong();

//        root.getChildren().add(ambientLight);
        root.getChildren().add(light);
        this.setCamera(camera);
        this.setFocusTraversable(true);

        this.setOnKeyPressed((KeyEvent k) -> {
            if (k.getCode() == KeyCode.SPACE) {
                spaceIsPressed = true;
            }
        });

        this.setOnKeyReleased((KeyEvent k) -> {
            if (k.getCode() == KeyCode.SPACE) {
                spaceIsPressed = false;
            }
        });

        addScrollEvent();

        this.setOnMousePressed((MouseEvent me) -> {
            mousePressTime.set(System.currentTimeMillis());
            mouseOldX = me.getSceneX();
            mouseOldY = me.getSceneY();
            PickResult pr = me.getPickResult();
            this.requestFocus();
            if (pr!=null && pr.getIntersectedNode() != null && pr.getIntersectedNode() instanceof Sphere) {
                distance=pr.getIntersectedDistance();
                s = (Sphere) pr.getIntersectedNode();
                isPicking=true;
                vecIni = unProjectDirection(mousePosX, mousePosY, this.getWidth(), this.getHeight());
            }
        });

        this.setOnMouseDragged((MouseEvent me) -> {
            mousePosX = me.getSceneX();
            mousePosY = me.getSceneY();
            if (isPicking) {
                vecPos = unProjectDirection(mousePosX, mousePosY, this.getWidth(), this.getHeight());
                Point3D p=vecPos.subtract(vecIni).multiply(distance);
                s.getTransforms().add(new Translate(p.getX(),p.getY(),p.getZ()));
                vecIni=vecPos;
                PickResult pr = me.getPickResult();
                if(pr!=null && pr.getIntersectedNode() != null && pr.getIntersectedNode()==s){
                    distance=pr.getIntersectedDistance();
                } else {
                    isPicking=false;
                }
            } else if (spaceIsPressed) {
                camPosition.setX(camPosition.getX() - (mousePosX - mouseOldX) * camPosition.getZ() / -5000);
                camPosition.setY(camPosition.getY() - (mousePosY - mouseOldY) * camPosition.getZ() / -5000);
            } else {
                double newXRotate = rotateX.getAngle() - 0.5 * (mousePosY - mouseOldY);
                double newYRotate = rotateY.getAngle() - 0.5 * (mousePosX - mouseOldX);
                rotateX.setAngle(newXRotate);
                rotateY.setAngle(newYRotate);
            }
            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        });

        this.setOnMouseReleased((MouseEvent me)->{
            mousePressTime.set(System.currentTimeMillis() - mousePressTime.get());
            if(isPicking){
                isPicking=false;
            }
        });

        this.setOnMouseClicked(event -> {
            if (mousePressTime.get() < 200)
                root.getSelectionManager().setSelected(null);
        });
    }

    public int getScrollSpeed() {
       return this.scrollSpeed;
    }

    public void setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        addScrollEvent();
    }
    public boolean getIsDynamicScrollSpeed() {
        return this.isDynamicScrollSpeed;
    }
    public void  setIsDynamicScrollSpeed(boolean isDynamicScrollSpeed) {
        this.isDynamicScrollSpeed = isDynamicScrollSpeed;
        addScrollEvent();
    }
    private void addScrollEvent() {
        this.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double timeDiff = 1.;
            if (scrollNum == 0 ) scrollTime = Instant.now();
            if (ChronoUnit.MILLIS.between(scrollTime, Instant.now()) < 1000) {
                scrollNum +=1;
            } else scrollNum = 0;
            if (scrollNum >= 5) {
                timeDiff = ChronoUnit.MILLIS.between(scrollTime, Instant.now()) / 1000.0;
            }
            if (isDynamicScrollSpeed) {
                if (Math.abs(deltaY) >= (1 + scrollSpeed)) {
                    camPosition.setZ(camPosition.getZ() + (1 + scrollSpeed) * deltaY);
                }
            } else {
                if (Math.abs(deltaY) >= 0) {
                    camPosition.setZ(camPosition.getZ() + (5. / timeDiff) * (Math.abs(deltaY) / 10) * deltaY);
                }
            }
        });
    }

    /*
     From fx83dfeatures.Camera3D
     http://hg.openjdk.java.net/openjfx/8u-dev/rt/file/5d371a34ddf1/apps/toys/FX8-3DFeatures/src/fx83dfeatures/Camera3D.java
    */
    public Point3D unProjectDirection(double sceneX, double sceneY, double sWidth, double sHeight) {
        double tanHFov = Math.tan(Math.toRadians(camera.getFieldOfView()) * 0.5f);
        Point3D vMouse = new Point3D(tanHFov*(2*sceneX/sWidth-1), tanHFov*(2*sceneY/sWidth-sHeight/sWidth), 1);

        Point3D result = localToSceneDirection(vMouse);
        return result.normalize();
    }
    @Override
    public Point3D localToScene(Point3D pt) {
        Point3D res = camera.localToParentTransformProperty().get().transform(pt);
        if (camera.getParent() != null) {
            res = camera.getParent().localToSceneTransformProperty().get().transform(res);
        }
        return res;
    }

    public Point3D localToSceneDirection(Point3D dir) {
        Point3D res = localToScene(dir);
        return res.subtract(localToScene(new Point3D(0, 0, 0)));
    }
}
