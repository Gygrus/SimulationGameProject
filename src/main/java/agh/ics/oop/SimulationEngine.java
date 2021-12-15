package agh.ics.oop;

//import agh.ics.oop.gui.App;

import agh.ics.oop.gui.App;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;
import static java.util.Collections.sort;

public class SimulationEngine implements IEngine, Runnable {
    private boolean running = true;
    private int numOfLiving = 0;
    public int globalCount = 0;
    public ArrayList<Integer> deadLifeSpan = new ArrayList<Integer>();
    public HashMap<ArrayList<Integer>, Integer> allGenes = new HashMap<ArrayList<Integer>, Integer>();
    private MoveDirection[] directions;
    private AbstractWorldMap map;
    private List<Animal> animals = new ArrayList<>();
    private App guiObserver;
    public int initialEnergy;
    private ArrayList<Animal> deadAnimals = new ArrayList<>();
    private GridPane gridpane;
    private VBox gridData;

    public SimulationEngine(AbstractWorldMap map, int startingNumber, int energy, GridPane gridpane, VBox gridData) {
        this.gridData = gridData;
        this.gridpane = gridpane;
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
        }
    }

    public void addDeadLifeSpan(int span){
        this.deadLifeSpan.add(span);
    }

    public int getNumOfLiving() { return this.numOfLiving; }

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
        if (this.animals.size() == 0){
            return 0;
        }
        return sum/this.animals.size();
    }

    public double getAverageChildren(){
        double sum = 0;
        for (Animal animal : this.animals){
            sum += animal.getChildren();
        }
        if (this.animals.size() == 0){
            return 0;
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
        if (output == null){
            return null;
        }
        return output.toString() + " " + max;
    }

    public void addAnimal(Animal animal) {
        this.numOfLiving += 1;
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
        this.numOfLiving -= 1;
        this.animals.remove(animal);
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

    public void guiUpdate() {
        this.guiObserver.guiUpdate(getAllVisible(), this.map, this, this.gridpane, this.gridData);
    }

    public List<Animal> getAnimals() { return this.animals;}

    public void switchRunning() {
        this.running = !this.running;
//        if (!this.running){
//            try {
//                this.wait();
//            } catch (InterruptedException ex){
//                out.println(ex);
//            }
//        }
    }

    public boolean getRunning() {
        return this.running;
    }

    public void run(){
        while (true) {
            if (this.running) {
                this.map.deleteBodies();
                for (Animal animal : this.getAnimals()) {
                    animal.move();
                }
                this.map.animalEat();
                this.map.reproduce();
                this.map.addBushes();
                guiUpdate();
                continue;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }

        }
    }
}
