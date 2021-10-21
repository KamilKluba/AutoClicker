package data;

import javafx.geometry.Point2D;

import java.io.Serializable;

public class Point implements Serializable {
    private int x;
    private int y;

    public Point(){
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPoint(java.awt.Point point){
        this.x = point.x;
        this.y = point.y;
    }

    public java.awt.Point getPoint(){
        return new java.awt.Point(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
