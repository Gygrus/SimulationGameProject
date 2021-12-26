package agh.ics.oop;

import com.sun.source.tree.Tree;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver, GlobalValues {
    protected boolean isMagic;
    protected int magicCount = 4;
    private Animal trackedAnimal;
    public int energyLoss;
    private int currentGen = 0;
    protected ArrayList<Vector2d> bushesSavanna = new ArrayList<Vector2d>();
    protected ArrayList<Vector2d> bushesJungle = new ArrayList<Vector2d>();
    protected ArrayList<Vector2d> bushesAll = new ArrayList<Vector2d>();
    protected int bushEnergy;
    protected boolean borders;
    protected int width;
    protected int height;
    protected Vector2d jungleLowerLeft;
    protected Vector2d jungleUpperRight;
    protected Vector2d lowerLeft;
    protected Vector2d upperRight;
    protected int numOfBushes = 0;
    private HashMap<Vector2d, ArrayList<Animal>> animals = new HashMap<Vector2d, ArrayList<Animal>>();
    protected HashMap<Vector2d, Grass> bushes = new HashMap<Vector2d, Grass>();
    private final MapVisualizer visualizer = new MapVisualizer(this);
    private SimulationEngine engineObserver;

    public SimulationEngine getEngineObserver() { return this.engineObserver; }

    public boolean getIsMagic() { return this.isMagic; }

    public int getMagicCount() { return this.magicCount; }

    public void setMagicCount(int number) { this.magicCount = number; }

    public int getCurrentGen() { return this.currentGen; }

    public String toString() {return visualizer.draw(lLeftGet(), uRightGet());}

    public HashMap<Vector2d, ArrayList<Animal>> getAnimals(){
        return this.animals;
    }

    public void setTrackedAnimal(Animal animal) { this.trackedAnimal = animal; }

    public boolean getBorders() { return this.borders; }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public Vector2d lLeftGet(){ return this.lowerLeft; }

    public Vector2d uRightGet(){ return this.upperRight; }

    public HashMap<Vector2d, Grass> getBushes(){ return new HashMap<>(); }

    public void addEngineObserver(SimulationEngine engineObserver) { this.engineObserver = engineObserver; }

    public boolean checkIfInJungle(Vector2d position){
        return position.getCordX() >= jungleLowerLeft.getCordX() && position.getCordX() <= jungleUpperRight.getCordX()
                && position.getCordY() >= jungleLowerLeft.getCordY()
                && position.getCordY() <= jungleUpperRight.getCordY();
    }

    protected void generateBushesPosition() {
        for (int i = 0; i <= this.getWidth(); i++){
            for (int j = 0; j <= this.getHeight(); j++){
                Vector2d vecToAdd = new Vector2d(i, j);
                if (checkIfInJungle(vecToAdd)){
                    this.bushesJungle.add(vecToAdd);
                } else {
                    this.bushesSavanna.add(vecToAdd);
                }
                this.bushesAll.add(vecToAdd);
            }
        }
    }

    protected void addBushes() {
        List<Vector2d> availablePosJungle = new ArrayList<>(this.bushesJungle);
        for (Vector2d pos: this.animals.keySet()){
            availablePosJungle.remove(pos);
        }
        if (!availablePosJungle.isEmpty()) {
            Vector2d newPos1 = availablePosJungle.get(ThreadLocalRandom.current().nextInt(0, availablePosJungle.size()));
            Grass bushJungle = new Grass(newPos1);
            this.bushesJungle.remove(newPos1);
            this.bushesAll.remove(newPos1);
            this.bushes.put(newPos1, bushJungle);
            this.numOfBushes += 1;
        }
        List<Vector2d> availablePosSavanna = new ArrayList<>(this.bushesSavanna);
        for (Vector2d pos: this.animals.keySet()){
            availablePosSavanna.remove(pos);
        }
        if (!availablePosSavanna.isEmpty()) {
            Vector2d newPos2 = availablePosSavanna.get(ThreadLocalRandom.current().nextInt(0, availablePosSavanna.size()));
            Grass bushSavanna = new Grass(newPos2);
            this.bushesSavanna.remove(newPos2);
            this.bushesAll.remove(newPos2);
            this.bushes.put(newPos2, bushSavanna);
            this.numOfBushes += 1;
        }
    }

    public void deleteBodies() {
        this.currentGen += 1;
        for (Vector2d position: this.animals.keySet()){
            int len = this.animals.get(position).size();
            while (!this.animals.get(position).isEmpty() && this.animals.get(position).get(len-1).getEnergy() <= 0){
                Animal curAnimal = this.animals.get(position).get(len-1);
                curAnimal.death = currentGen;
                this.engineObserver.addDeadLifeSpan(this.currentGen - curAnimal.birth);
                this.engineObserver.deleteAnimal(curAnimal);
                this.animals.get(position).remove(len-1);
                len -= 1;
            }
        }

        this.animals.keySet().removeIf(key -> this.animals.get(key).isEmpty());
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        this.animals.get(oldPosition).remove(animal);
        if (this.animals.get(oldPosition).isEmpty()){
            this.animals.remove(oldPosition);
        }
        if (this.animals.containsKey(newPosition)){
            this.animals.get(newPosition).add(animal);
        } else {
            ArrayList<Animal> newList = new ArrayList<>();
            newList.add(animal);
            this.animals.put(newPosition, newList);
        }
    }

    public boolean canMoveTo (Vector2d position) {

        if (!this.borders) {
            return true;
        }
        return position.precedes(this.upperRight) && position.follows(this.lowerLeft);
    }

    // for initial animals
    public boolean place(Animal animal) {
        if (!isOccupied(animal.getPosition())){
            animal.addObserver(this);
            ArrayList<Animal> newList = new ArrayList<>();
            newList.add(animal);
            Integer val = this.engineObserver.getAllGenes().get(animal.getGenes());
            this.engineObserver.getAllGenes().put(animal.getGenes(), val == null ? 1 : val + 1);
            this.engineObserver.addAnimal(animal);
            this.animals.put(animal.getPosition(), newList);
            return true;
        }
        return false;
    }

    // for new animals - children
    public void placeNewChild(Animal animal){
        animal.addObserver(this);
        Integer val = this.engineObserver.getAllGenes().get(animal.getGenes());
        this.engineObserver.getAllGenes().put(animal.getGenes(), val == null ? 1 : val + 1);
        this.animals.get(animal.getPosition()).add(animal);
        this.engineObserver.addAnimal(animal);
    }

    public boolean isOccupied(Vector2d position) {
        return this.animals.get(position) != null;
    }

    public ArrayList<Animal> objectAt(Vector2d position) {
        return this.animals.get(position);
    }

    public ArrayList<Animal> getStrongest(ArrayList<Animal> animals){
        ArrayList<Animal> output = new ArrayList<>();
        int max = animals.get(0).getEnergy();
        for (Animal animal: animals){
            if (max == animal.getEnergy()){
                output.add(animal);
            } else {
                break;
            }
        }
        return output;
    }

    public ArrayList<Animal> getPair(ArrayList<Animal> animals) {
        ArrayList<Animal> bestAnimals = new ArrayList<>();
        int max = animals.get(0).getEnergy();

        for (Animal animal: animals){
            if (max == animal.getEnergy() && animal.getEnergy() >= this.engineObserver.initialEnergy*(0.5)){
                bestAnimals.add(animal);
            } else if (bestAnimals.size() < 2 && animal.getEnergy() >= this.engineObserver.initialEnergy*(0.5)) {
                bestAnimals.add(animal);
            } else {
                break;
            }
        }

        ArrayList<Animal> output = new ArrayList<Animal>();
        if (bestAnimals.size() >= 2){
            output.add(0, bestAnimals.get(0));
            output.add(1, bestAnimals.get(1));
        }
        return output;
    }

    public void animalEat(){

        for (Vector2d position: this.animals.keySet()){
            this.animals.get(position).sort(comparatorAnimal);
            if (this.bushes.containsKey(position)){
                ArrayList<Animal> strongest = this.getStrongest(this.animals.get(position));
                int len = strongest.size();

                for (Animal animal: strongest){
                    animal.changeEnergy(this.bushEnergy/len);
                }

                this.bushes.put(position, null);
                this.numOfBushes -= 1;

                if (this.checkIfInJungle(position)){
                    this.bushesJungle.add(position);
                } else {
                    this.bushesSavanna.add(position);
                }
                this.bushesAll.add(position);
            }
        }
        this.bushes.keySet().removeIf(key -> this.bushes.get(key) == null);
    }

    public void reproduce(){
        for (ArrayList<Animal> neighbors: this.animals.values()){
            if (neighbors.size() >= 2){
                ArrayList<Animal> pair = this.getPair(neighbors);
                if (pair.size() >= 2){

                    //creating new child
                    Animal newChild = pair.get(0).createNewLife(pair.get(1), this.currentGen);

                    //tracking descendants
                    if (this.trackedAnimal != null && (pair.get(0).getTrackedAnimal() == this.trackedAnimal ||
                            pair.get(1).getTrackedAnimal() == this.trackedAnimal)){
                        newChild.setTrackedAnimal(this.trackedAnimal);
                        this.trackedAnimal.addDescendant();
                    }

                    //adding to map
                    this.placeNewChild(newChild);
                }
            }

        }

    }
}
