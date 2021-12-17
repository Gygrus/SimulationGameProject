package agh.ics.oop;

//import agh.ics.oop.gui.App;

import agh.ics.oop.gui.App;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;
import static java.util.Collections.sort;

public class SimulationEngine implements IEngine, Runnable {
    private int moveDelay;
    private boolean running = true;
    private int numOfLiving = 0;
    private int globalCount = 0;
    private ArrayList<Integer> deadLifeSpan = new ArrayList<Integer>();
    private HashMap<ArrayList<Integer>, Integer> allGenes = new HashMap<ArrayList<Integer>, Integer>();
    private AbstractWorldMap map;
    private List<Animal> animals = new ArrayList<>();
    private App guiObserver;
    public int initialEnergy;
    private ArrayList<Animal> deadAnimals = new ArrayList<>();
    private GridPane gridpane;
    private VBox gridData;
    private DataStatistics dataManager;

    public SimulationEngine(AbstractWorldMap map, int startingNumber, int energy, GridPane gridpane, VBox gridData, int moveDelay){
        this.moveDelay = moveDelay;
        this.gridData = gridData;
        this.gridpane = gridpane;
        this.initialEnergy = energy;
        this.map = map;
        this.dataManager = new DataStatistics(this.map.getBorders());
        this.map.addEngineObserver(this);
        this.spawnAnimals(startingNumber);
    }

    public int getMoveDelay() { return this.moveDelay; }

    public HashMap<ArrayList<Integer>, Integer> getAllGenes() { return this.allGenes; }

    public int getGlobalCount() { return this.globalCount; }

    public void addGlobalCount(int i) { this.globalCount += i; }

    private void spawnAnimals(int number){
        boolean check = false;
        if (this.animals.size() == 5){
            check = true;
        }
        for (int i = 0; i < number; i++) {
            ArrayList<Integer> genes = new ArrayList<Integer>();
            if (!check) {
                for (int j = 0; j < 32; j++) {
                    genes.add(ThreadLocalRandom.current().nextInt(0, 8));
                }
            } else {
                genes = new ArrayList<>(this.animals.get(i).getGenes());
            }

            sort(genes);
            Vector2d newPosition = new Vector2d(ThreadLocalRandom.current().nextInt(0, this.map.getWidth()+1), ThreadLocalRandom.current().nextInt(0, this.map.getHeight()+1));
            while (this.map.getAnimals().containsKey(newPosition) && this.map.getBushes().containsKey(newPosition)) {
                newPosition.setCords(ThreadLocalRandom.current().nextInt(0, this.map.getWidth()+1), ThreadLocalRandom.current().nextInt(0, this.map.getHeight()+1));
            }
            Animal toAdd = new Animal(this.map, newPosition, genes, this.initialEnergy, globalCount);
            this.globalCount += 1;
            this.map.place(toAdd);
        }
    }

    public void addDeadLifeSpan(int span){
        this.deadLifeSpan.add(span);
    }

    public int getNumOfLiving() { return this.numOfLiving; }

    public double updateAverageDeadLifeSpan(){
        if (this.deadLifeSpan.isEmpty()){return 0;}
        double sum = 0;
        for (int span : this.deadLifeSpan){
            sum += span;
        }
        return sum/this.deadLifeSpan.size();
    }

    public double updateAverageEnergy(){
        double sum = 0;
        for (Animal animal : this.animals){
            sum += animal.getEnergy();
        }
        if (this.animals.size() == 0){
            return 0;
        }
        return sum/this.animals.size();
    }

    public double updateAverageChildren(){
        double sum = 0;
        for (Animal animal : this.animals){
            sum += animal.getChildren();
        }
        if (this.animals.size() == 0){
            return 0;
        }
        return sum/this.animals.size();
    }

    public ArrayList<Number> getStatisticsData() {
        ArrayList<Number> output = new ArrayList<>();
        output.add(this.dataManager.getAnimalData());
        output.add(this.dataManager.getBushData());
        output.add(this.dataManager.getEnergyData());
        output.add(this.dataManager.getLifeSpanData());
        output.add(this.dataManager.getChildrenData());
        return output;
    }

    private void sendStatisticsData() {
        this.dataManager.addStatistics(this.getNumOfLiving(), this.map.getBushes().size(), this.updateAverageEnergy(), this.updateAverageDeadLifeSpan(), this.updateAverageChildren());
    }

    public void writeDataToFile() {
        try {
            this.dataManager.writeToFile();
        } catch (IOException ignored) {
        }
    }

    public ArrayList<Integer> getMostCommonGenes(){
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
        return output;
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

    public ArrayList<MapObject> getAllWithGenes(ArrayList<Integer> genes){
        ArrayList<MapObject> output = new ArrayList<MapObject>();
        for (TreeSet<Animal> animals : this.map.getAnimals().values()){
            for (Animal animal : animals){
                if (animal.getGenes().equals(genes)){
                    output.add(animal);
                    break;
                }
            }
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
    }

    public boolean getRunning() {
        return this.running;
    }

    public void run(){
        while (true) {
            if (this.running) {
                this.sendStatisticsData();
                long startTime = System.nanoTime();
                this.map.deleteBodies();
                if (this.map.getIsMagic() && this.map.getMagicCount() > 0 && this.animals.size() == 5){
                    this.spawnAnimals(5);
                    this.map.setMagicCount(this.map.getMagicCount()-1);
                    this.guiObserver.magicConditionInfo(this);
                }
                long postDelete = System.nanoTime();
                out.println("Usuwanie: "+(postDelete-startTime)/1000000);
                for (Animal animal : this.getAnimals()) {
                    animal.move();
                }
                long postMove = System.nanoTime();
                out.println("Poruszanie: "+(postMove-postDelete)/1000000);
                this.map.animalEat();
                long postEat = System.nanoTime();
                out.println("Jedzenie: "+(postEat-postMove)/1000000);
                this.map.reproduce();
                long postCopulate = System.nanoTime();
                out.println("Rozmnażanie: "+(postCopulate-postDelete)/1000000);
                this.map.addBushes();
                long postBushes = System.nanoTime();
                out.println("Krzaki: "+(postBushes-postCopulate)/1000000);
                guiUpdate();
                try {
                    Thread.sleep(this.moveDelay);
                } catch (InterruptedException ex){
                    out.println(ex);
                }
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
