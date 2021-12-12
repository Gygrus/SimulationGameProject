package agh.ics.oop;


public class RectangularMap extends AbstractWorldMap {
    private final Vector2d lowerLeft = new Vector2d(0,0);
    private final Vector2d upperRight;

    public RectangularMap (int width, int height) {
        this.upperRight = new Vector2d(width, height);
    }

    @Override public Vector2d lLeftGet() {
        return this.lowerLeft;
    }

    @Override public Vector2d uRightGet() {
        return this.upperRight;
    }

    @Override public boolean canMoveTo (Vector2d position) {
        return super.canMoveTo(position) && position.follows(lowerLeft) && position.precedes(upperRight);
    }

}
