package agh.ics.oop;

public class Grass extends MapObject {
    public Grass(Vector2d position){
        this.position = position;
    }

    public String toString(){return "*";}

    @Override
    public String getInputStream() {
        return "src/main/resources/grass.png";
    }

    @Override
    public String getLabel() { return "Trawa"; }
}
