package agh.ics.oop;

//import agh.ics.oop.gui.App;

import agh.ics.oop.gui.App;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;
import static java.util.Collections.sort;

public class SimulationEngine implements IEngine, Runnable {
    public int globalCount = 0;
    public ArrayList<Integer> deadLifeSpan = new ArrayList<Integer>();
    public HashMap<ArrayList<Integer>, Integer> allGenes = new HashMap<ArrayList<Integer>, Integer>();
    protected int numOfDead = 0;
    protected int numOfBushes = 0;
    private MoveDirection[] directions;
    private AbstractWorldMap map;
    private List<Animal> animals = new ArrayList<>();
    private App guiObserver;
    public int initialEnergy;
    private ArrayList<Animal> deadAnimals = new ArrayList<>();

    public SimulationEngine(AbstractWorldMap map, int startingNumber, int energy) {
        this.initialEnergy = energy;
        this.map = map;
        this.map.setEngineObserver(this);
        for (int i = 0; i < startingNumber; i++) {
            ArrayList<Integer> genes = new ArrayList<Integer>();
            for (int j = 0; j < 32; j++) {
                genes.add(ThreadLocalRandom.current().nextInt(0, 8));
            }

            sort(genes);
            Vector2d newPosition = new Vector2d(ThreadLocalRandom.current().nextInt(0, this.map.getWidth()+1), ThreadLocalRandom.current().nextInt(0, this.map.getHeight()+1));
            while (this.map.getAnimals().containsKey(newPosition)) {
                newPosition.setCords(ThreadLocalRandom.current().nextInt(0, this.map.getWidth()+1), ThreadLocalRandom.current().nextInt(0, this.map.getHeight()+1));
            }
            Animal toAdd = new Animal(this.map, newPosition, genes, this.initialEnergy, 0, this.globalCount);
            this.globalCount += 1;
            this.map.place(toAdd);
            this.animals.add(toAdd);
        }
    }

    public void addDeadLifeSpan(int span){
        this.deadLifeSpan.add(span);
    }

    public double getAverageDeadLifeSpan(){
        if (this.deadLifeSpan.isEmpty()){return 0;}
        double sum = 0;
        for (int span : this.deadLifeSpan){
            sum += span;
        }
        return sum/this.deadLifeSpan.size();
    }

    public double getAverageEnergy(){
        double sum = 0;
        for (Animal animal : this.animals){
            sum += animal.getEnergy();
        }
        return sum/this.animals.size();
    }

    public double getAverageChildren(){
        double sum = 0;
        for (Animal animal : this.animals){
            sum += animal.getChildren();
        }
        return sum/this.animals.size();
    }

    public String getMostCommonGenes(){
        int max = 0;
        ArrayList<Integer> output = null;

        for (ArrayList<Integer> genes : this.allGenes.keySet()){
            if (this.allGenes.get(genes) >= max){
                output = genes;
                max = this.allGenes.get(genes);
            }
        }
        return output.toString() + " " + max;
    }

    public void addAnimal(Animal animal) {
        this.animals.add(animal);
    }

    public void deleteAnimal(Animal animal){
        ArrayList<Integer> genesToRemove = animal.getGenes();

        int sum = 0;
        for (Animal animal1 : this.getAnimals()){
            if (animal1.getGenes() == genesToRemove){
                sum += 1;
            }
        }

        Integer val = this.allGenes.get(genesToRemove);
        this.allGenes.put(genesToRemove, val - 1);
        if (this.allGenes.get(genesToRemove) <= 0){
            this.allGenes.remove(genesToRemove);
        }
        this.animals.remove(animal);
    }

    public void addDeadAnimals(Animal animal) {
        this.deadAnimals.add(animal);
        this.numOfDead += 1;
    }

    public void sendPositions(App observer) {

    }

    public void addGuiObserver(App observer){
        this.guiObserver = observer;
    }

    private ArrayList<MapObject> getAllVisible(){
        ArrayList<MapObject> output = new ArrayList<MapObject>();
        for (TreeSet<Animal> animals : this.map.getAnimals().values()){
            output.add(animals.first());
        }

        for (Grass bush : this.map.getBushes().values()){
            if (!this.map.getAnimals().containsKey(bush.getPosition())){
                output.add(bush);
            }
        }
        return output;
    }

    public void setDirections(MoveDirection[] directions) {this.directions = directions;}

    public void guiUpdate() {
        AbstractWorldMap newMap = this.map;
        this.guiObserver.guiUpdate(getAllVisible(), newMap);
    }

    public List<Animal> getAnimals() { return this.animals;}

    public void run(){
        while (true) {
            this.map.deleteBodies();
            for (Animal animal : this.getAnimals()) {
                animal.move();
            }
            this.map.animalEat();
            this.map.reproduce();
            this.map.addBushes();
            guiUpdate();
//            try {
//                Thread.sleep(300);
//            } catch (InterruptedException ex) {
//                out.println(ex);
//            }
//            out.println(this.getAnimals());
//            out.println(this.map.getAnimals());
            out.println(this.map.getNumOfAnimals() == this.getAnimals().size());
//            out.println(this.getAnimals().size());
//            out.println(this.map.bushes);

        }
    }
}
