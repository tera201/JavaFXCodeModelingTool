package org.tera201.elements.circle;

public interface AddNewPosition {

    class Point {

        double x;
        double z;
        double r;
        double angle;

        public Point() {
            this(0, 0, 1, 0);
        }
        Point(double x, double z, double r, double angle) {
            setPoint(x, z, r, angle);
        }

        public void reset() {
            x = 0;
            z = 0;
            r = 1;
            angle = 0;
        }

        public void setPoint(double x, double z, double r, double angle) {
            this.x = x;
            this.z = z;
            this.r = r;
            this.angle = angle;
        }

        public double getX() {
            return x;
        }

        public double getZ() {
            return z;
        }

        public double getR() {
            return r;
        }

        public double getAngle() {
            return angle;
        }
    }
}
