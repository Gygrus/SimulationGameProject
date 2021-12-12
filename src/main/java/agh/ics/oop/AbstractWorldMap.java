package agh.ics.oop;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;

public abstract class AbstractWorldMap implements IWorldMap, IPositionChangeObserver, GlobalValues {
    public int energyLoss;
    public int currentGen = 0;
    public ArrayList<Vector2d> bushesSavanna;
    public ArrayList<Vector2d> bushesJungle;
    public int bushEnergy;
    public int initialEnergy;
    public boolean borders;
    public int width;
    public int height;
    public Vector2d jungleLowerLeft;
    public Vector2d jungleUpperRight;
    public Vector2d lowerLeft;
    public Vector2d upperRight;
    public int numOfLiving = 0;
    public int numOfDead = 0;
    public HashMap<Vector2d, TreeSet<Animal>> animals = new HashMap<Vector2d, TreeSet<Animal>>();
    public HashMap<Vector2d, Grass> bushes = new HashMap<Vector2d, Grass>();
    public MapVisualizer visualizer = new MapVisualizer(this);
    public ArrayList<Animal> deadAnimals = new ArrayList<Animal>();
    public ArrayList<Animal> newAnimals = new ArrayList<Animal>();
    public SimulationEngine engineObserver;

    public String toString() {return visualizer.draw(lLeftGet(), uRightGet());}

    public HashMap<Vector2d, TreeSet<Animal>> getAnimals(){
        return this.animals;
    }

    public boolean getBorders() { return this.borders; }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public Vector2d lLeftGet(){ return this.lowerLeft; }

    public Vector2d uRightGet(){ return this.upperRight; }

    public HashMap<Vector2d, Grass> getBushes(){ return new HashMap<>(); }

    public void addEngineObserver(SimulationEngine engineObserver) { this.engineObserver = engineObserver; }

    private void generateBushesPosition() {
        for (int i = 0; i <= this.getWidth(); i++){
            for (int j = 0; j <= this.getHeight(); j++){
                if (i >= jungleLowerLeft.getCordX() && i <= jungleUpperRight.getCordX() && j >= jungleLowerLeft.getCordY() && j <= jungleUpperRight.getCordY()){
                    this.bushesJungle.add(new Vector2d(i, j));
                } else {
                    this.bushesSavanna.add(new Vector2d(i, j));
                }
            }
        }
    }

    private void addBushes() {
        Vector2d newPos1 = this.bushesJungle.get(ThreadLocalRandom.current().nextInt(0, this.bushesJungle.size()));
        while (this.animals.containsKey(newPos1)){
            newPos1 = this.bushesJungle.get(ThreadLocalRandom.current().nextInt(0, this.bushesJungle.size()));
        }
        Grass bushJungle = new Grass(newPos1);
        this.bushes.put(newPos1, bushJungle);
        Vector2d newPos2 = this.bushesSavanna.get(ThreadLocalRandom.current().nextInt(0, this.bushesSavanna.size()));
        while (this.animals.containsKey(newPos2)){
            newPos2 = this.bushesSavanna.get(ThreadLocalRandom.current().nextInt(0, this.bushesSavanna.size()));
        }
        Grass bushSavanna = new Grass(newPos2);
        this.bushes.put(newPos2, bushSavanna);
    }

    public void setEngineObserver(SimulationEngine engine) { this.engineObserver = engine; }

    public void deleteBodies() {
        for (Vector2d position: this.animals.keySet()){
            TreeSet<Animal> curSet = this.animals.get(position);
            while (!curSet.isEmpty() && curSet.last().getEnergy() <= 0){
                this.engineObserver.addDeadAnimals(curSet.last());
                curSet.remove(curSet.last());
            }
        }

        this.animals.keySet().removeIf(key -> this.animals.get(key).isEmpty());
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        Animal movedAnimal = null;
        for (Animal an: this.animals.get(oldPosition)){
            if (animal.equals(an)){
                movedAnimal = an;
                this.animals.get(oldPosition).remove(an);
                if (this.animals.get(oldPosition).isEmpty()){
                    this.animals.remove(oldPosition);
                }
                break;
            }
        }
        if (this.animals.containsKey(newPosition)){
            this.animals.get(newPosition).add(movedAnimal);
        } else {
            TreeSet<Animal> newList = new TreeSet<Animal>(comparatorAnimal);
            newList.add(movedAnimal);
            this.animals.put(newPosition, newList);
        }
    }

    public boolean canMoveTo (Vector2d position) {

        if (!this.getBorders()) {
            return true;
        }
        if (!(position.precedes(this.upperRight)&&position.follows(this.lowerLeft))){
            return false;
        }
        return true;
    }

    public boolean place(Animal animal) {
        if (!isOccupied(animal.getPosition())){
            animal.addObserver(this);
            this.numOfLiving += 1;
            TreeSet<Animal> newList = new TreeSet<Animal>(comparatorAnimal);
            newList.add(animal);
            this.animals.put(animal.getPosition(), newList);
            return true;
        }
        return false;
    }

    public void placeNewChild(Animal animal){
        animal.addObserver(this);
        this.newAnimals.add(animal);
        this.animals.get(animal.getPosition()).add(animal);
        this.numOfLiving += 1;
    }

    public boolean isOccupied(Vector2d position) {
        return this.animals.get(position) != null;
    }

    public TreeSet<Animal> objectAt(Vector2d position) {
        return this.animals.get(position);
    }

    public ArrayList<Animal> getStrongest(TreeSet<Animal> animals){
        ArrayList<Animal> output = new ArrayList<Animal>();
        int max = animals.first().getEnergy();
        for (Animal animal: animals){
            if (max == animal.getEnergy()){
                output.add(animal);
            } else {
                break;
            }
        }
        return output;
    }

    public ArrayList<Animal> getPair(TreeSet<Animal> animals) {
        int premax = -1;
        TreeSet<Animal> bestAnimals = new TreeSet<Animal>(comparatorAnimal);
        int max = animals.first().getEnergy();
        for (Animal animal: animals){
            if (max == animal.getEnergy() && animal.getEnergy() > 0){
                bestAnimals.add(animal);
            } else if (bestAnimals.size() < 2 && animal.getEnergy() > 0) {
                bestAnimals.add(animal);
            } else {
                break;
            }
        }

        ArrayList<Animal> output = new ArrayList<Animal>();
        output.add(0, bestAnimals.pollFirst());
        output.add(1, bestAnimals.pollFirst());

        return output;
    }

    public void animalEat(){
        for (Vector2d position: this.bushes.keySet()){
            if (this.animals.containsKey(position)){
                ArrayList<Animal> strongest = this.getStrongest(this.animals.get(position));
                int len = strongest.size();
                for (Animal animal: strongest){
                    animal.changeEnergy(this.bushEnergy/len);
                }
                this.bushes.put(position, null);
//                this.bushes.remove(position);
            }
        }
        this.bushes.keySet().removeIf(key -> this.bushes.get(key) == null);

    }

    public void reproduce(){
        for (TreeSet<Animal> neighbors: this.animals.values()){
            if (neighbors.size() >= 2){
                ArrayList<Animal> pair = this.getPair(neighbors);
                if (pair.size() >= 2){
                    ArrayList<Integer> newGenes = new ArrayList<Integer>();
                    int sum = pair.get(0).getEnergy() + pair.get(1).getEnergy();
                    if (ThreadLocalRandom.current().nextInt(0, 2) == 1){
                        for (int i = 0; i < 32; i++){
                            if (i < (pair.get(1).getEnergy()/sum)*32){
                                newGenes.add(pair.get(1).getGenes().get(i));
                            } else {
                                newGenes.add(pair.get(0).getGenes().get(i));
                            }
                        }
                    } else {
                        for (int i = 0; i < 32; i++){
                            if (i < (pair.get(0).getEnergy()/sum)*32){
                                newGenes.add(pair.get(0).getGenes().get(i));
                            } else {
                                newGenes.add(pair.get(1).getGenes().get(i));
                            }
                        }
                    }
                    Collections.sort(newGenes);
                    int energy = pair.get(0).getEnergy()/4 + pair.get(1).getEnergy()/4;
                    pair.get(0).changeEnergy(-pair.get(0).getEnergy()/4);
                    pair.get(1).changeEnergy(-pair.get(1).getEnergy()/4);
                    Animal newChild = new Animal(this, pair.get(0).getPosition(), newGenes, energy, this.currentGen);
                    this.placeNewChild(newChild);
                }
            }
        }
    }


}
