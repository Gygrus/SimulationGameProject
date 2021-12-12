package agh.ics.oop;

import java.util.Objects;

public class Vector2d {
    public int x;
    public int y;
    public Vector2d(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getCordX(){
        return this.x;
    }

    public int getCordY(){
        return this.y;
    }

    public void correctCords(int width, int height){
        this.x = (this.x+width)%(width);
        this.y = (this.y+height)%(height);
    }

    public void setCords(int newX, int newY){
        this.x = newX;
        this.y = newY;
    }

    public String toString(){
        return "("+this.x+","+this.y+")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public boolean precedes(Vector2d other){
        return this.x <= other.x && this.y <= other.y;
    }

    public boolean follows(Vector2d other){
        return this.x >= other.x && this.y >= other.y;
    }

    public Vector2d upperRight(Vector2d other){
        int x1, y1;
        x1 = (Math.max(this.x, other.x));
        y1 = (Math.max(this.y, other.y));
        return new Vector2d(x1, y1);
    }

    public Vector2d lowerLeft(Vector2d other){
        int x1, y1;
        x1 = (Math.min(this.x, other.x));
        y1 = (Math.min(this.y, other.y));
        return new Vector2d(x1, y1);
    }

    public Vector2d add(Vector2d other){
        return new Vector2d(this.x+other.x, this.y+other.y);
    }

    public Vector2d subtract(Vector2d other){
        return new Vector2d(this.x-other.x, this.y-other.y);
    }

    public boolean equals(Object other){
        if (this == other)
            return true;

        if (!(other instanceof Vector2d))
            return false;


        Vector2d that = (Vector2d) other;
        return this.x == that.x && this.y == that.y;
    }

    public Vector2d opposite(){
        return new Vector2d(- this.x, - this.y);
    }
}
