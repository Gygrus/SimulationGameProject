package agh.ics.oop.gui;

import agh.ics.oop.GrassField;
import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.TreeSet;

import static java.lang.System.out;


public class App extends Application {
    private AbstractWorldMap map1, map2;
    private GridPane gridpane = new GridPane();
    private VBox gridData = new VBox();
    private GuiElementBox vBoxGenerator = new GuiElementBox();
    private int moveDelay = 100;
    private int numOfAnimals = 10;
    private Scene scene;
    private Thread thread;
    private SimulationEngine engine;

    @Override
    public void init() {
        this.map1 = new GrassField(10, 5, 5, 0.4F, false, 2, 10);
        this.map2 = new GrassField(10, 20, 20, 0.4F, true, 2, 10);
        this.engine = new SimulationEngine(this.map1, 20, 50);
        this.engine.addGuiObserver(this);
//        this.gridpane.setMinSize(500, 500);
//        this.gridpane.setMaxSize(500, 500);
        Thread curThread = new Thread(this.engine);
        curThread.setDaemon(true);
        curThread.start();
    }


    public void guiUpdate(ArrayList<MapObject> objects, AbstractWorldMap newMap){
        Platform.runLater(()->{
            this.gridpane.setGridLinesVisible(false);
            this.gridpane.getChildren().clear();
            this.gridpane.getColumnConstraints().clear();
            this.gridpane.getRowConstraints().clear();
            createGridPane(objects, newMap);
            fillData();
        });
        try {
            Thread.sleep(this.moveDelay);
        } catch (InterruptedException ex){
            out.println(ex);
        }
    }

    private void fillData() {
        this.gridData.getChildren().clear();
        Label living = new Label("Number of animals: " + this.engine.getAnimals().size());
        Label bushes = new Label("Number of grass: " + this.map1.numOfBushes);
        Label genes = new Label("Dominating genes: " + this.engine.getMostCommonGenes());
        Label avEnergy = new Label("Average energy: " + this.engine.getAverageEnergy());
        Label avSpan = new Label("Average lifespan for dead: " + this.engine.getAverageDeadLifeSpan());
        Label avChildren = new Label("Average number of children: " + this.engine.getAverageChildren());
        this.gridData.getChildren().addAll(living, bushes, avSpan, genes, avEnergy, avChildren);
    }

//    private ArrayList<MapObject> getAllVisible(AbstractWorldMap newMap){
//        ArrayList<MapObject> output = new ArrayList<MapObject>();
//        for (TreeSet<Animal> animals : newMap.getAnimals().values()){
//            output.add(animals.first());
//        }
//
//        for (Grass bush : newMap.getBushes().values()){
//            if (!newMap.getAnimals().containsKey(bush.getPosition())){
//                output.add(bush);
//            }
//        }
//        return output;
//    }


    private void createGridPane(ArrayList<MapObject> objects, AbstractWorldMap newMap) {
        this.gridpane.setGridLinesVisible(true);
        Vector2d lowerLeft = newMap.lLeftGet();
        Vector2d upperRight = newMap.uRightGet();

        int width = upperRight.getCordX() - lowerLeft.getCordX() + 1;
        int height = upperRight.getCordY() - lowerLeft.getCordY() + 1;

        ColumnConstraints colConst = new ColumnConstraints(500/width);
        RowConstraints rowConst = new RowConstraints(500/height);

        for (int i = 0; i < width; i++){
            this.gridpane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < height; i++){
            this.gridpane.getRowConstraints().add(rowConst);
        }


//        for (MapObject animal : newMap.getAnimals().values()){
//            this.gridpane.add(this.vBoxGenerator.generateVBox(animal), animal.getPosition().getCordX() - lowerLeft.getCordX() + 1, upperRight.getCordY()-(animal.getPosition().getCordY())+1);
//
//        }
//
//        for (MapObject bush : newMap.getBushes().values()){
//            this.gridpane.add(this.vBoxGenerator.generateVBox(bush), bush.getPosition().getCordX() - lowerLeft.getCordX() + 1, upperRight.getCordY()-(bush.getPosition().getCordY())+1);
//        }

        for (MapObject obj : objects){
            this.gridpane.add(this.vBoxGenerator.generateVBox(obj), obj.getPosition().getCordX() - lowerLeft.getCordX(), upperRight.getCordY()-(obj.getPosition().getCordY()));
        }



    }

    private void addOtherGUI(int width, int height) {
        HBox inputStart = new HBox();
        TextField inputValue = new TextField();
        Button simStart = new Button("Start");
        simStart.setOnAction(e -> {
            MoveDirection[] directions = new OptionsParser().parse(inputValue.getText().split(" "));
            this.engine.setDirections(directions);
            Thread curThread = new Thread(this.engine);
            curThread.setDaemon(true);
            curThread.start();
        });
        inputStart.getChildren().addAll(inputValue, simStart);
        this.gridpane.add(inputStart, width+3, 0, 2, 3);
    }

    public void start(Stage primaryStage){
//        createGridPane(this.map1);
        VBox layout = new VBox();
        layout.setSpacing(120);
        layout.getChildren().addAll(gridpane, gridData);
//        layout.add(test);
        Scene scene = new Scene(layout, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
