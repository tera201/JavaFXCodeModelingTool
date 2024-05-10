package org.tera201.elements.circle;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import org.tera201.SelectionManager;
import org.tera201.elements.Selectable;
import org.tera201.elements.SelectionObserver;
import org.tera201.elements.SpaceListObject;
import org.tera201.elements.SpaceObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PackageCircle extends HollowCylinder implements SpaceListObject<HollowCylinder>, AddNewPosition,
        Selectable, SelectionObserver {
    private String name;
    private String path = "";
    private Tooltip tooltip;
    private final Point lastPoint = new Point();
    private final Group group = new Group();
    private final List<CirclePosition> circlePositionList = new ArrayList<>();
    private Map<String, HollowCylinder> circles = new HashMap<>();
    private List<HollowCylinder> orderList;
    private final Color defaultColor = Color.DARKGRAY;
    private SelectionManager selectionManager;
    private String filePath;

    public PackageCircle(String name, double radiusOuter, double radiusInner, double height) {
        super(radiusOuter, radiusInner, height);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(defaultColor);
        material.setSpecularColor(Color.BLACK);
        setMaterial(material);
        group.getChildren().add(this);
        this.name = name;
        this.path = name;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getObjectPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void setPosition(double newX, double newZ) {
        setTranslateX(newX);
        setTranslateZ(newZ);
    }

    private Tooltip ensureTooltip() {
        if(tooltip == null) {
            tooltip = new Tooltip(name);
            Tooltip.install(this, tooltip);
        }
        return tooltip;
    }

    @Override
    public void setNotes(String notes) {
        ensureTooltip().setText(notes);
    }

    @Override
    public void setSelectionManager(SelectionManager selectionManager) {
        circles.values().forEach(it -> ((SpaceObject) it).setSelectionManager(selectionManager));
        AtomicLong mousePressTime = new AtomicLong();
        this.selectionManager = selectionManager;
        if (selectionManager != null)
            selectionManager.addObserver(this);

        this.setOnMousePressed(event -> mousePressTime.set(System.currentTimeMillis()));
        this.setOnMouseReleased(event -> mousePressTime.set(System.currentTimeMillis() - mousePressTime.get()));
        this.setOnMouseClicked(event -> {
            if (selectionManager != null && mousePressTime.get() < 200) {
                this.selectionManager.setSelected(this);
            }
            event.consume();  // stop event propagation
        });
    }

    @Override
    public void setHighlighted(boolean highlighted) {
        ((PhongMaterial) getMaterial()).setDiffuseColor(highlighted ? Color.RED : defaultColor);
    }

    @Override
    public String getInfo() {
        return circles.values().stream().map(it ->
            ((SpaceObject) it).getName() + " r:" + it.getInnerRadius() + " R:" + it.getOuterRadius() + "\n"
        ).collect(Collectors.joining()) + path;
    }

    @Override
    public String getHeader() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    public Map<String, HollowCylinder> getNestedCircles() {
        return circles;
    }

    @Override
    public void addObject(HollowCylinder circle) {
        circles.put(((SpaceObject) circle).getName(), circle);
        if (circle instanceof PackageCircle packageCircle) {
            group.getChildren().add(packageCircle.getGroup());
            packageCircle.setPath(path + ":" + packageCircle.getName());
        }
        else if (circle instanceof ClassCircle classCircle){
            group.getChildren().add(classCircle);
            classCircle.setPath(path + ":" + classCircle.getName());
        }

        setCirclePosition(circle);
        ((SpaceObject) circle).setSelectionManager(selectionManager);
    }

    @Override
    public SpaceObject findObjectByPath(String path) {
        if (this.path.equals(path)) {return this;}
        else return (SpaceObject) circles.values().stream().map(circle -> {
            if (((SpaceObject) circle).getPath().equals(path)) {
                return circle;
            } else if (path.startsWith(((SpaceObject) circles).getPath()) && (circle instanceof PackageCircle packageCircle)) {
                    return packageCircle.findObjectByPath(path);

            } else return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public void addAllObjects(HollowCylinder... circles) {
        Arrays.stream(circles).forEach(this::addObject);
    }

    public void setCirclePosition(HollowCylinder circle) {
        double newAngle = lastPoint.getX() != 0? genNewAngle(circle) : 0;
        adjustBuildingPosition(circle, newAngle);
        updateLastPointWith(circle, newAngle);
    }

    private void updateLastPointWith(HollowCylinder circle, double angle) {
        lastPoint.setPoint(circle.getTranslateX(), circle.getTranslateZ(), circle.getOuterRadius(), angle);
    }

    private double getNestedCircleXByAngle(HollowCylinder nestedCircle, Double maxR, Double angle) {
        return (maxR  - nestedCircle.getOuterRadius()) * Math.cos(angle);
    }

    private double getNestedCircleZByAngle(HollowCylinder nestedCircle, Double maxR, Double angle) {
        return (maxR - nestedCircle.getOuterRadius()) * Math.sin(angle);
    }

    private Double getStraightLineLen(Double x1, Double z1, Double x2, Double z2) {
        Double a1 = x2 - x1;
        Double a2 = z2 - z1;
        return Math.sqrt(a1 * a1 + a2 * a2);
    }

    private void bindCircle(HollowCylinder circle, Double angle) {
        circle.translateXProperty().bind(translateXProperty().add(innerRadiusProperty().subtract(circle.outerRadiusProperty()).multiply(Math.cos(angle))));
        circle.translateZProperty().bind(translateZProperty().add(innerRadiusProperty().subtract(circle.outerRadiusProperty()).multiply(Math.sin(angle))));
        circle.translateYProperty().bind(translateYProperty());
    }

    private double genNewAngle(HollowCylinder circle) {
        double a = getInnerRadius() - circle.getOuterRadius();
        double b = getInnerRadius() - lastPoint.getR();
        double c = circle.getOuterRadius() + lastPoint.getR();
        return lastPoint.getAngle() + Math.acos((a*a + b*b - c*c) / (2 * a * b));
    }

    private boolean checkLinesBtNestedCirclesCenters() {
        double x0 = circlePositionList.get(0).centerX;
        double z0 = circlePositionList.get(0).centerZ;
        double r0 = circlePositionList.get(0).radius;
        for (int c = 1; c < circlePositionList.size(); c++) {
            Double centerLine = getStraightLineLen(x0, z0, circlePositionList.get(c).centerX, circlePositionList.get(c).centerZ);
            if (centerLine.isNaN() || (centerLine < (r0 + circlePositionList.get(c).radius) - 0.001)) return false;
        }
        return true;
    }

    private double getAngleForRadius(List<HollowCylinder> orderList, double radius) {
        circlePositionList.clear();
        double angle = 0.;
        Point point = new Point();
        point.setPoint(0, 0, orderList.get(0).getOuterRadius(), angle);
        circlePositionList.add(new CirclePosition(getNestedCircleXByAngle(orderList.get(0), radius, angle),
                getNestedCircleZByAngle(orderList.get(0), radius, angle),
                orderList.get(0).getOuterRadius()));
        double radiusSquared = radius * radius;
        for (int i = 1; i < orderList.size(); i++) {
            angle += Math.acos((radiusSquared - orderList.get(i).getOuterRadius() * radius - point.getR() * (radius + orderList.get(i).getOuterRadius())) /
                    ((radius - orderList.get(i).getOuterRadius())*(radius - point.getR())));
            circlePositionList.add(new CirclePosition(getNestedCircleXByAngle(orderList.get(i), radius, angle),
                    getNestedCircleZByAngle(orderList.get(i), radius, angle),
                    orderList.get(i).getOuterRadius()));
            point.setPoint(0, 0, orderList.get(i).getOuterRadius(), angle);
        }
        angle += Math.acos((radiusSquared - orderList.get(0).getOuterRadius() * radius - point.getR() * (radius + orderList.get(0).getOuterRadius())) /
                ((radius - orderList.get(0).getOuterRadius())*(radius - point.getR())));
        return Math.toDegrees(angle);
    }

    private  void updateCircleOrder() {
        orderList = circles.values().stream().sorted(Comparator.comparingDouble(HollowCylinder::getOuterRadius).reversed()).toList();
    }

    private double getOptimalRadius(boolean experimental) {
//        double step = 1000;
        updateCircleOrder();
//        int tries = 0;
//        double minR =  2 * orderList.get(0).getOuterRadius();
        double step = experimental?  orderList.get(0).getOuterRadius() : 1000;
        double minR = experimental? orderList.stream().mapToDouble(HollowCylinder::getOuterRadius).sum() : 2 * orderList.get(0).getOuterRadius();
        System.out.println("experimental: " + experimental);
//        System.out.println("minR: " + minR);
        double gap = getAngleGap(getAngleForRadius(orderList, minR));
        double oldGap = 0;
        boolean nestedRadiusFit = checkLinesBtNestedCirclesCenters();
        byte changed = (byte) (gap > 0 ? 2 : 1);
        while ((gap < 0 || gap > 10) && step > 10 || !nestedRadiusFit && experimental) {
//            tries++;
            if (gap < 0 || !nestedRadiusFit) {
                changed = (byte) (changed == 2 ? 0 : 1);
                step = changed == 0 ? step/2 : step;
                minR += step;
                changed = 1;
            } else {
                changed = (byte) (changed == 1 ? 0 : 2);
                step = changed == 0 ? step/2 : step;
                minR -= step;
                changed = 2;
            }
            gap = getAngleGap(getAngleForRadius(orderList, minR));
            nestedRadiusFit = checkLinesBtNestedCirclesCenters();
            if (Double.isNaN(gap)){
                minR += changed==1?-step:step;
                step /= 2;
                gap = oldGap;
            }
            oldGap = gap;
        }
//        System.out.println("tries: " + tries + " gap: " + gap + " nested radius fit: " + nestedRadiusFit);
        return minR;
    }

    private double getAngleGap(double sumAngle) {
        return 360 - sumAngle;
    }

    private void adjustBuildingPosition(HollowCylinder circle, double angle) {
        bindCircle(circle, angle);
    }

    private void nestedOptimize() {
        circles.values().stream().filter(PackageCircle.class::isInstance).map(PackageCircle.class::cast).forEach(PackageCircle::nestedOptimize);
        if (circles.size() == 1 && circles.values().stream().findFirst().get() instanceof PackageCircle packageCircle) {
            name += ":" + packageCircle.getName();
            path = packageCircle.getPath();
            packageCircle.group.getChildren().clear();
            Map<String, HollowCylinder> nestedCircles = packageCircle.getNestedCircles();
            clear();
            nestedCircles.values().forEach(this::addObject);
            packageCircle.clear();
            circles.remove(packageCircle.path);
        }
    }

    @Override
    public void removeObject(HollowCylinder circle) {
        group.getChildren().remove(circle);
        circles.remove(((SpaceObject) circle).getName());
        selectionManager.removeObserver(((SelectionObserver) circle));
    }

    @Override
    public void removeAllObjects(HollowCylinder... circles) {
        Arrays.stream(circles).forEach(this::removeObject);
    }


    // TODO: maybe call for nested circles, but needs refactoring for nestedOptimize
    @Override
    public void clear() {
        circles.clear();
        lastPoint.reset();
        group.getChildren().clear();
        group.getChildren().add(this);

    }
    public void updateView() {
        updateView(false);
    }

    @Override
    public void updateView(boolean experimental) {
        nestedOptimize();
        updateCircleOrder();
        orderList.stream().filter(PackageCircle.class::isInstance).map(PackageCircle.class::cast).forEach(it -> it.updateView(experimental));
        if (!orderList.isEmpty()) {
            double optimalR = getOptimalRadius(experimental);
            double border = getOuterRadius() - getInnerRadius();
            setInnerRadius(optimalR);
            setOuterRadius(optimalR + border);
            lastPoint.reset();
            orderList.forEach(this::setCirclePosition);
            orderList.forEach(it -> ((SpaceObject) it).setSelectionManager(selectionManager));
        }
    }

    @Override
    public void onSelectionChanged(Selectable newSelection) {
        if (this.equals(newSelection) || newSelection == null ) {
            this.setVisible(true);
        } else {
            String selectedPath = ((SpaceObject)newSelection).getPath();
            if (getFirstPathNode(selectedPath).equals(getFirstPathNode(path)) ||
                    getPathWithoutFirstNode(selectedPath).equals(getPathWithoutFirstNode(path)))
                this.setVisible(true);
            else this.setVisible(false);
        }
    }

    private String getFirstPathNode(String path) {
        int dotIndex = path.indexOf(':');
        if (dotIndex != -1) {
            return path.substring(0, dotIndex);
        }
        return path;
    }

    private String getPathWithoutFirstNode(String path) {
        int dotIndex = path.lastIndexOf(':');
        if (dotIndex != -1) {
            return path.substring(dotIndex, path.length() - 1);
        }
        return path;
    }

    private static class CirclePosition {
        double centerX;
        double centerZ;
        double radius;
        public CirclePosition(double centerX, double centerZ, double radius) {
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.radius = radius;
        }
    }
}
