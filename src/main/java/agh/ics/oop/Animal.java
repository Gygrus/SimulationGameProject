package agh.ics.oop;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class Animal extends MapObject implements GlobalValues {
    public int id;
    public int numOfChildren = 0;
    public int birth, death;
    private int energy;
    private ArrayList<Integer> genes;
    private Integer orientation;
    private AbstractWorldMap map;
    private List<IPositionChangeObserver> observerList = new ArrayList<>();


    public Animal(AbstractWorldMap map, Vector2d initialPosition, ArrayList<Integer> genes, int energy, int birth, int id) {
        this.id = id;
        this.birth = birth;
        this.energy = energy;
        this.genes = genes;
        this.map = map;
        this.position = initialPosition;
        this.orientation = this.genes.get(ThreadLocalRandom.current().nextInt(0, 32));
    }

    @Override
    public String getInputStream() {
        return switch (this.orientation) {
            case 0 -> "src/main/resources/up.png";
            case 4 -> "src/main/resources/down.png";
            case 2 -> "src/main/resources/right.png";
            case 6 -> "src/main/resources/left.png";
            default -> "src/main/resources/up.png";
        };
    }

//    @Override
//    public boolean equals(Animal o){
//        if ((this == o) || (this.id == o.id)){
//            return true;
//        }
//        return false;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public ArrayList<Integer> getGenes() { return this.genes; }

    @Override
    public String getLabel(){
        return this.position.toString() + this.energy;
    }

    public int getChildren() { return this.numOfChildren; }

    public int getEnergy() { return this.energy; }

    public void changeEnergy(int value) { this.energy = this.energy + value; }

    protected void addObserver(IPositionChangeObserver observer) {
        this.observerList.add(observer);
    }

    private void removeObserver(IPositionChangeObserver observer) {
        this.observerList.remove(observer);
    }

    private void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver observerList: this.observerList){
            observerList.positionChanged(oldPosition, newPosition, this);
        }
    }


    public String toString(){
        return switch (this.orientation) {
            case 0 -> "N";
            case 4 -> "S";
            case 2 -> "E";
            case 6 -> "W";
            case 3 -> "SE";
            case 5 -> "SW";
            case 1 -> "NE";
            case 7 -> "NW";
            default -> "none";
        };
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public void move() {
        int randomNum = this.genes.get(ThreadLocalRandom.current().nextInt(0, 32));
        Vector2d newPositionA, newPositionB;
//        out.println(this.position);
//        out.println(this.toString());
//        out.println(randomNum);
        switch (randomNum) {
            case 0 -> {
                newPositionA = this.position.add(dirToVec[this.orientation]);
//                out.println(newPositionA);
                if (!this.map.getBorders()){
                    newPositionA.correctCords(this.map.getWidth(), this.map.getHeight());
                }
//                out.println(newPositionA);
                if (this.map.canMoveTo(newPositionA)) {
                    this.energy -= this.map.energyLoss;
                    this.positionChanged(this.position, newPositionA);
                    this.position = newPositionA;
                }
            }
            case 4 -> {
                newPositionB = this.position.subtract(dirToVec[this.orientation]);
                if (!this.map.getBorders()){
                    newPositionB.correctCords(this.map.getWidth(), this.map.getHeight());
                }
                if (this.map.canMoveTo(newPositionB)) {
                    this.energy -= this.map.energyLoss;
                    this.positionChanged(this.position, newPositionB);
                    this.position = newPositionB;
                }
            }
            default -> {
                this.energy -= this.map.energyLoss;
                this.orientation = (this.orientation+randomNum)%8;
            }
        }
    }

}
