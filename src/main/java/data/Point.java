package data;

import javafx.geometry.Point2D;

import java.io.Serializable;

public class Point implements Serializable {
    private double x;
    private double y;

    public Point(){
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setPoint(Point2D point){
        this.x = point.getX();
        this.y = point.getY();
    }

    public Point2D getPoint(){
        return new Point2D(x, y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
