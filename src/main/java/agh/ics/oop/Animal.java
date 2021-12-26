package agh.ics.oop;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class Animal extends MapObject implements GlobalValues {
    private Animal trackedAnimal;
    private int descendantsNumber = 0;
    public int id;
    private int numOfChildren = 0;
    public int birth, death;
    private int energy;
    private final ArrayList<Integer> genes;
    private Integer orientation;
    private final AbstractWorldMap map;
    private final List<IPositionChangeObserver> observerList = new ArrayList<>();


    public Animal(AbstractWorldMap map, Vector2d initialPosition, ArrayList<Integer> genes, int energy, int birth) {
        this.birth = birth;
        this.death = 0;
        this.energy = energy;
        this.genes = genes;
        this.map = map;
        this.position = initialPosition;
        this.orientation = this.genes.get(ThreadLocalRandom.current().nextInt(0, 32));
    }


    public AbstractWorldMap getMap(){ return this.map; }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    public ArrayList<Integer> getGenes() { return this.genes; }

    public int getDescendantsNumber() { return this.descendantsNumber; }

    public void addDescendant() {this.descendantsNumber += 1;}

    public void setTrackedAnimal(Animal animal){
        this.trackedAnimal = animal;
        this.numOfChildren = 0;
    }

    public Animal getTrackedAnimal() { return this.trackedAnimal; }

    public int getChildren() { return this.numOfChildren; }

    private void addChildren() { this.numOfChildren = this.numOfChildren + 1; }

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

    public Animal createNewLife(Animal partner, int currentGen){
        ArrayList<Integer> newGenes = new ArrayList<>();
        int sum = this.getEnergy() + partner.getEnergy();
        if (ThreadLocalRandom.current().nextInt(0, 2) == 1){
            for (int i = 0; i < 32; i++){
                if (i < (partner.getEnergy()/sum)*32){
                    newGenes.add(partner.getGenes().get(i));
                } else {
                    newGenes.add(this.genes.get(i));
                }
            }
        } else {
            for (int i = 0; i < 32; i++){
                if (i < (this.energy/sum)*32){
                    newGenes.add(this.genes.get(i));
                } else {
                    newGenes.add(partner.getGenes().get(i));
                }
            }
        }
        Collections.sort(newGenes);
        int energy = this.energy/4 + partner.getEnergy()/4;
        this.changeEnergy(-this.energy/4);
        partner.changeEnergy(-partner.getEnergy()/4);
        this.addChildren();
        partner.addChildren();
        return new Animal(this.map, this.position, newGenes, energy, currentGen);
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
        int randomGene = this.genes.get(ThreadLocalRandom.current().nextInt(0, 32));
        Vector2d newPosition;
        switch (randomGene) {
            case 0 -> {
                newPosition = this.position.add(dirToVec[this.orientation]);
                moveYourself(newPosition);
            }
            case 4 -> {
                newPosition = this.position.subtract(dirToVec[this.orientation]);
                moveYourself(newPosition);
                }
            default -> {
                this.energy -= this.map.energyLoss;
                this.orientation = (this.orientation+randomGene)%8;
            }
        }
    }

    private void moveYourself(Vector2d newPosition) {
        if (!this.map.getBorders()){
            newPosition.correctCords(this.map.getWidth(), this.map.getHeight());
        }
        if (this.map.canMoveTo(newPosition)) {
            this.energy -= this.map.energyLoss;
            Vector2d oldPosition = this.position;
            this.position = newPosition;
            this.positionChanged(oldPosition, this.position);
        }
    }

}
