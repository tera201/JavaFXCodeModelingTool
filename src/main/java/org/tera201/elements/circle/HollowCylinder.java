package org.tera201.elements.circle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

public class HollowCylinder extends MeshView {
    static final int DEFAULT_DIVISIONS = 64;
    static final double DEFAULT_RADIUS_OUTER= 2;
    static final double DEFAULT_RADIUS_INNER = 1;
    static final double DEFAULT_HEIGHT = 2;

    private final int divisions;
    private TriangleMesh mesh;
    private DoubleProperty height;
    private DoubleProperty outerRadius;
    private DoubleProperty innerRadius;

    public HollowCylinder()  {
        this(DEFAULT_RADIUS_OUTER, DEFAULT_RADIUS_INNER, DEFAULT_HEIGHT, DEFAULT_DIVISIONS);
    }

    public HollowCylinder(double radiusOuter, double radiusInner, double height) {
        this(radiusOuter, radiusInner, height, DEFAULT_DIVISIONS);
    }

    public HollowCylinder(double radiusOuter, double radiusInner, double height, int divisions) {
        super();
        this.divisions = Math.max(divisions, 3);
        setOuterRadius(radiusOuter);
        setInnerRadius(radiusInner);
        setHeight(height);
        mesh = createMesh((float) radiusOuter, (float) radiusInner, (float) height, divisions);
        this.setMesh(mesh);
        heightProperty().addListener((observable, oldValue, newValue) -> updateMesh());
        innerRadiusProperty().addListener((observable, oldValue, newValue) -> updateMesh());
        outerRadiusProperty().addListener((observable, oldValue, newValue) -> updateMesh());
    }

    public final void setHeight(double value) {
        heightProperty().set(value);
    }

    public final double getHeight() {
        return height == null ? 2 : height.get();
    }

    public final DoubleProperty heightProperty() {
        if (height == null) {
            height = new SimpleDoubleProperty(HollowCylinder.this, "height", DEFAULT_HEIGHT);
        }
        return height;
    }

    public final void setRadius(float radiusOuter, float radiusInner) {
        outerRadiusProperty().set(radiusOuter);
        innerRadiusProperty().set(radiusInner);
    }

    public final void setOuterRadius(double value) {
        outerRadiusProperty().set(value);
    }

    public final double getOuterRadius() {
        return outerRadius == null ? DEFAULT_RADIUS_OUTER : outerRadius.get();
    }

    public final DoubleProperty outerRadiusProperty() {
        if (outerRadius == null) {
            outerRadius = new SimpleDoubleProperty(HollowCylinder.this, "outerRadius");
        }
        return outerRadius;
    }

    public final void setInnerRadius(double value) {
        innerRadiusProperty().set(value);
    }

    public final double getInnerRadius() {
        return innerRadius == null ? DEFAULT_RADIUS_INNER : innerRadius.get();
    }

    public final DoubleProperty innerRadiusProperty() {
        if (innerRadius == null) {
            innerRadius = new SimpleDoubleProperty(HollowCylinder.this, "innerRadius");
        }
        return innerRadius;
    }

    public int getDivisions() {
        return divisions;
    }


    private void updateMesh() {
        mesh = createMesh((float) getOuterRadius(), (float) getInnerRadius(), (float) getHeight(), divisions);
        this.setMesh(mesh);
    }

    private static TriangleMesh createMesh(float radiusOuter, float radiusInner, float height, int div) {
        TriangleMesh mesh = new TriangleMesh();

        float dA = 1.f / div;
        float textureDelta = 1.f / 256;

        // top edge
        for (int i = 0; i <= div; i++) {
            double angle = 2 * Math.PI * i / div;
            float xOuter = radiusOuter * (float) Math.cos(angle);
            float yOuter = radiusOuter * (float) Math.sin(angle);

            float xInner = radiusInner * (float) Math.cos(angle);
            float yInner = radiusInner * (float) Math.sin(angle);

            mesh.getPoints().addAll(xOuter, height / 2, yOuter);
            mesh.getPoints().addAll(xInner, height / 2, yInner);
            mesh.getTexCoords().addAll( 1 - dA * i, 1 - textureDelta);
        }

        // top edge
        mesh.getTexCoords().addAll( 0, 1 - textureDelta);

        // bottom edge
        for (int i = 0; i <= div; i++) {
            double angle = 2 * Math.PI * i / div;
            float xOuter = radiusOuter * (float) Math.cos(angle);
            float yOuter = radiusOuter * (float) Math.sin(angle);

            float xInner = radiusInner * (float) Math.cos(angle);
            float yInner = radiusInner * (float) Math.sin(angle);

            mesh.getPoints().addAll(xOuter, -height / 2, yOuter);
            mesh.getPoints().addAll(xInner, -height / 2, yInner);
            mesh.getTexCoords().addAll( 1 - dA * i, 1 - textureDelta);
        }

        // bottom edge
        mesh.getTexCoords().addAll( 0, textureDelta);

        // add cap central points
        mesh.getPoints().addAll(0, height, 0, 0, -height, 0);

        // add cap central points
        // bottom cap
        for (int i = 0; i <= div; ++i) {
            double a = (i < div) ? (dA * i * 2) * Math.PI: 0;
            mesh.getTexCoords().addAll((float) (Math.sin(a) * 0.5f) + 0.5f, (float) (Math.cos(a) * 0.5f) + 0.5f);
        }

        // top cap
        for (int i = 0; i <= div; ++i) {
            double a = (i < div) ? (dA * i * 2) * Math.PI: 0;
            mesh.getTexCoords().addAll(0.5f + (float) (Math.sin(a) * 0.5f), 0.5f - (float) (Math.cos(a) * 0.5f));
        }

        mesh.getTexCoords().addAll(0.5f, 0.5f);

        for (int i = 0; i < div; i++) {
            int next = (i + 1) % div;
            int idxOuter1 = i * 2;
            int idxInner1 = idxOuter1 + 1;
            int idxOuter2 = next * 2;
            int idxInner2 = idxOuter2 + 1;
            int nextCr = div * 2 + 2;

            // top cap
            mesh.getFaces().addAll(idxOuter1, 0, idxInner1, 0, idxInner2, 0);
            mesh.getFaces().addAll(idxInner2, 0, idxOuter2, 0, idxOuter1, 0);
            mesh.getFaceSmoothingGroups().addAll(1, 1);

            // bottom cap
            mesh.getFaces().addAll(idxOuter1 + nextCr, 0, idxInner2+ nextCr, 0, idxInner1+ nextCr, 0);
            mesh.getFaces().addAll(idxInner2 + nextCr, 0, idxOuter1 + nextCr, 0, idxOuter2 + nextCr, 0);
            mesh.getFaceSmoothingGroups().addAll(2, 2);

            // outer cylinder
            mesh.getFaces().addAll(idxOuter1, 0, idxOuter2 + nextCr, 0, idxOuter1 + nextCr, 0);
            mesh.getFaces().addAll(idxOuter2 + nextCr, 0, idxOuter1, 0, idxOuter2, 0);
            mesh.getFaceSmoothingGroups().addAll(3, 3);

            // inner cylinder
            mesh.getFaces().addAll(idxInner1, 0, idxInner1 + nextCr, 0, idxInner2 + nextCr, 0);
            mesh.getFaces().addAll(idxInner2 + nextCr, 0, idxInner2, 0, idxInner1, 0);
            mesh.getFaceSmoothingGroups().addAll(4, 4);
        }

        return mesh;
    }

}
