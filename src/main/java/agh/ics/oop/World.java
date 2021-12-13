package agh.ics.oop;
//import agh.ics.oop.gui.App;
import agh.ics.oop.gui.App;
import com.sun.source.tree.Tree;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;

import static java.lang.System.out;

public class World {
    public static void main(String[] args){
//        try {
//            String[] list = {"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
//            MoveDirection[] directions = new OptionsParser().parse(args);
//            IWorldMap map1 = new GrassField(5);
//            IWorldMap map2 = new RectangularMap(10, 10);
//            Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(3, 4)};
//            IEngine engine = new SimulationEngine(directions, map2, positions);
//            engine.run();
//            Application.launch(App.class, args);
//        } catch (IllegalArgumentException ex) {
//            out.println(ex);
//        }
//        AbstractWorldMap mapa = new GrassField(5, 10, 10, 0.4F, false, 3, 15);
//        Animal test1 = new Animal(mapa, new Vector2d(0, 0), new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)), 50, 0);
//        Animal test2 = new Animal(mapa, new Vector2d(0, 1), new ArrayList<Integer>(Arrays.asList(4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4)), 50, 0);
//        SimulationEngine engine = new SimulationEngine(mapa, 70, 100);
//        mapa.setEngineObserver(engine);
//        TreeSet<Integer> test = new TreeSet<Integer>();
//        test.add(0);
//        test.add(2);
//        test.add(3);
//        test.add(5);
//        while (test.last() > 1){
//            test.pollLast();
//        }
        Application.launch(App.class);
    }


}
