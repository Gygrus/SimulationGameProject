package agh.ics.oop;

//import agh.ics.oop.gui.App;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.out;
import static java.util.Collections.sort;

public class SimulationEngine implements IEngine, Runnable {
    protected int numOfDead = 0;
    protected int numOfLiving = 0;
    private MoveDirection[] directions;
    private AbstractWorldMap map;
    private List<Animal> animals = new ArrayList<>();
//    private App guiObserver;
    private int initialEnergy;
    private ArrayList<Animal> deadAnimals = new ArrayList<>();

    public SimulationEngine(AbstractWorldMap map, int startingNumber, int energy) {
        this.initialEnergy = energy;
        this.map = map;
        for (int i = 0; i < startingNumber; i++) {
            ArrayList<Integer> genes = new ArrayList<Integer>();
            for (int j = 0; j < 32; j++) {
                genes.add(ThreadLocalRandom.current().nextInt(0, 8));
            }
            sort(genes);
            out.println(genes);
            Vector2d newPosition = new Vector2d(ThreadLocalRandom.current().nextInt(0, this.map.getWidth()), ThreadLocalRandom.current().nextInt(0, this.map.getHeight()));
            out.println(newPosition);
            out.println(this.map.getWidth());
            out.println(this.map.getHeight());
            while (this.map.getAnimals().containsKey(newPosition)) {
                newPosition.setCords(ThreadLocalRandom.current().nextInt(0, this.map.getWidth()), ThreadLocalRandom.current().nextInt(0, this.map.getHeight()));
            }
            Animal toAdd = new Animal(this.map, newPosition, genes, this.initialEnergy, 0);
            this.map.place(toAdd);
            this.animals.add(toAdd);
        }
    }

    public void addChild(Animal animal) {
        this.animals.add(animal);
        this.numOfLiving += 1;
    }

    public void addDeadAnimals(Animal animal) {
        this.deadAnimals.add(animal);
        this.numOfDead += 1;
        this.numOfLiving -= 1;
    }

//    public void addGuiObserver(App observer){
//        this.guiObserver = observer;
//    }

    public void setDirections(MoveDirection[] directions) {this.directions = directions;}

//    public void guiUpdate(IWorldMap map) {
//        AbstractWorldMap newMap = (AbstractWorldMap) map;
//        this.guiObserver.guiUpdate(newMap);
//    }

    public List<Animal> getAnimals() { return this.animals;}

    public void run(){
        int i = 0;
        for (Animal animal: this.getAnimals()) {
//            this.animals.get(i%(this.animals.size())).move();
//            guiUpdate(this.map);
//            i += 1;
            animal.move();
            out.println(animal.getEnergy());
        }
    }
}
