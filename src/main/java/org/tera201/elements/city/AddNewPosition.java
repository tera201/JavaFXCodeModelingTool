package org.tera201.elements;

import javafx.scene.shape.Box;

public interface AddNewPosition {

    class Point {

        double x;
        double z;
        double maxZ;

        Box main;
        double separate;
        public Point(Box main, double separate){
            this.main = main;
            this.separate = separate;
            setPoint(0, 0);
        }
        Point(double x, double y) {
            this.x = x;
            this.z = y;
        }

        public void reset() {
            x = 0;
            z = 0;
            maxZ = 0;
        }

        public void setPoint(double x, double z) {
            this.x = x;
            this.z = z;
        }

        public void setX(double x) {
            this.x = x + separate;
        }

        public double getX() {
            return x;
        }

        public double getX(double width) {
            if (needNewLine(width)) {
                x =  0;
                z += maxZ + separate;
            }
            return x;
        }

        public boolean needNewLine(double width) {
            return x + width > main.getWidth();
        }

        public double getReminderToBorder(double width) {
            if (needNewLine(width))
                return main.getWidth() - width;
            else
                return main.getWidth() - x - width;
        }

        public double getZ() {
            return z;
        }

        public void setMaxZ(double z) {
            maxZ = Math.max(z, maxZ);
        }

        public double getMaxZ() {
            return maxZ;
        }
    }
}
