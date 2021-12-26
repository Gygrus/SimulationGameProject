package agh.ics.oop.gui;

import agh.ics.oop.GrassField;
import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class App extends Application {
    private AbstractWorldMap map1, map2;
    private final GridPane gridpane1 = new GridPane();
    private final GridPane gridpane2 = new GridPane();
    private final VBox gridData1 = new VBox();
    private final VBox gridData2 = new VBox();
    private final VBox animalDetails1 = new VBox();
    private final VBox animalDetails2 = new VBox();
    private final Button showGenes = new Button("Show animals with most common genes for borderless");
    private final Button showGenes2 = new Button("Show animals with most common genes for borders");
    private final Button submitData1 = new Button("Submit Statistics for borderless");
    private final Button submitData2 = new Button("Submit Statistics for borders");
    private Animal trackedAnimal1 = null;
    private Animal trackedAnimal2 = null;
    private final GuiElementBox vBoxGenerator = new GuiElementBox();
    private SimulationEngine engine1, engine2;
    private final LineChart<String, Number> dataChart1 = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
    private final LineChart<String, Number> dataChart2 = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
    private final SimulationGuiMaker guiMaker = new SimulationGuiMaker(this.vBoxGenerator);
    private final MenuGui menuGuiMaker = new MenuGui(this);

    public int animalNumber1, animalNumber2;
    public int map1Width, map2Width;
    public int map1Height, map2Height;
    public int startEnergy1, startEnergy2;
    public int moveEnergy1, moveEnergy2;
    public int plantEnergy1, plantEnergy2;
    public float jungleRatio1, jungleRatio2;
    public boolean isMagic1, isMagic2;

    ScheduledExecutorService chartsExecutor2 = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService chartsExecutor1 = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void init() {
        this.vBoxGenerator.setAppObserver(this);
    }

    // for tracking clicked animal
    public void setTrackedAnimal(Animal animal){
        animal.setTrackedAnimal(animal);
        animal.getMap().setTrackedAnimal(animal);
        if (animal.getMap() == this.map1){
            this.trackedAnimal1 = animal;
            this.guiMaker.displayDetails(this.trackedAnimal1, this.animalDetails1);
        } else {
            this.trackedAnimal2 = animal;
            this.guiMaker.displayDetails(this.trackedAnimal2, this.animalDetails2);
        }
    }

    // info about magic condition being fulfilled and freezing simulation
    public void magicConditionInfo(SimulationEngine engine){
        Platform.runLater(()-> {
            Label info = new Label("Magic condition has occured! 5 animals duplicated");
            if (engine == this.engine1) {
                this.gridData1.getChildren().add(info);
            } else {
                this.gridData2.getChildren().add(info);
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignore){

        }
    }

    // generate frame
    public void guiUpdate(ArrayList<MapObject> objects, AbstractWorldMap newMap,
                          SimulationEngine engine, GridPane gridpane, VBox gridData){
        Platform.runLater(()->{
            ArrayList<Number> worldData = engine.getStatisticsData();
            gridpane.setGridLinesVisible(false);
            gridpane.getChildren().clear();
            gridpane.getColumnConstraints().clear();
            gridpane.getRowConstraints().clear();
            this.guiMaker.displayDetails(this.trackedAnimal1, this.animalDetails1);
            this.guiMaker.displayDetails(this.trackedAnimal2, this.animalDetails2);
            this.guiMaker.createGridPane(objects, newMap, gridpane);
            this.guiMaker.fillData(engine, gridData, worldData);
        });
    }

    // generate pause buttons
    private void createPauseButton(BorderPane pane){
        Button pauseButton1 = new Button("Pause/Resume");
        Button pauseButton2 = new Button("Pause/Resume");
        pauseButton1.setOnAction(e -> {
            this.engine1.switchRunning();
            this.guiMaker.displayDetails(this.trackedAnimal1, this.animalDetails1);
            this.showGenes.setVisible(!this.engine1.getRunning());
            this.submitData1.setVisible(!this.engine1.getRunning());
        });
        pauseButton2.setOnAction(e -> {
            this.engine2.switchRunning();
            this.guiMaker.displayDetails(this.trackedAnimal2, this.animalDetails2);
            this.showGenes2.setVisible(!this.engine2.getRunning());
            this.submitData2.setVisible(!this.engine2.getRunning());
        });
        HBox buttons = new HBox();
        buttons.getChildren().addAll(pauseButton1, pauseButton2);
        buttons.setSpacing(30);
        buttons.setAlignment(Pos.CENTER);
        pane.setTop(buttons);
    }

    // calculate moveDelay based on size of the map
    private int calculateMoveDelay(int width, int height){
        int size = width*height;
        if (size <= 400){
            return 20;
        } else if (size <= 2500){
            return 60;
        } else {
            return 100;
        }
    }

    // start simulation and display scene with given constraints
    public void showSimulation(){
        this.map1 = new GrassField(0, this.map1Width-1, this.map1Height-1, this.jungleRatio1,
                false, this.moveEnergy1, this.plantEnergy1, this.isMagic1);
        this.map2 = new GrassField(0, this.map2Width-1, this.map2Height-1, this.jungleRatio2,
                true, this.moveEnergy2, this.plantEnergy2, this.isMagic2);

        this.engine1 = new SimulationEngine(this.map1, this.animalNumber1, this.startEnergy1, this.gridpane1,
                this.gridData1, this.calculateMoveDelay(this.map1Width-1, this.map1Height-1));
        this.engine1.addGuiObserver(this);
        Thread curThread1 = new Thread(this.engine1);
        curThread1.setDaemon(true);

        this.engine2 = new SimulationEngine(this.map2, this.animalNumber2, this.startEnergy2, this.gridpane2,
                this.gridData2, this.calculateMoveDelay(this.map2Width-1, this.map2Height-1));
        this.engine2.addGuiObserver(this);
        Thread curThread2 = new Thread(this.engine2);
        curThread2.setDaemon(true);

        curThread1.start();
        curThread2.start();

        this.gridpane1.setAlignment(Pos.CENTER);
        this.gridpane2.setAlignment(Pos.CENTER);

        this.guiMaker.createDataPopulationChart(this.dataChart1, this.map1, this.chartsExecutor1, this.engine1);
        this.guiMaker.createDataPopulationChart(this.dataChart2, this.map2, this.chartsExecutor2, this.engine2);
        BorderPane mainPane = new BorderPane();

        VBox layoutFirst = new VBox();
        VBox layoutSecond = new VBox();
        this.guiMaker.positionLayouts(layoutFirst, this.gridpane1, this.gridData1, this.dataChart1);
        this.guiMaker.positionLayouts(layoutSecond, this.gridpane2, this.gridData2, this.dataChart2);

        mainPane.setLeft(layoutFirst);
        mainPane.setRight(layoutSecond);
        this.createPauseButton(mainPane);

        VBox animDetails = new VBox();
        this.guiMaker.createSymbolInfo(animDetails);

        this.guiMaker.createAnimalDetails(animDetails, this.submitData1, this.animalDetails1, this.showGenes, this.gridpane1,
                this.engine1, this.map1);
        this.guiMaker.createAnimalDetails(animDetails, this.submitData2, this.animalDetails2, this.showGenes2, this.gridpane2,
                this.engine2, this.map2);

        animDetails.setAlignment(Pos.CENTER);
        animDetails.setSpacing(20);
        mainPane.setCenter(animDetails);

        Scene simulationScene = new Scene(mainPane, 1600, 800);
        Stage simulationStage = new Stage();
        simulationStage.setOnCloseRequest(e -> {
            Platform.exit();
        });
        simulationStage.setFullScreen(true);
        simulationStage.setAlwaysOnTop(true);
        simulationStage.setScene(simulationScene);
        simulationStage.show();

    }

    public void start(Stage primaryStage){
        primaryStage.setScene(this.menuGuiMaker.generateScene());
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        chartsExecutor1.shutdownNow();
        chartsExecutor2.shutdownNow();
    }

}
