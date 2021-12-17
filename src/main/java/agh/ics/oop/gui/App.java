package agh.ics.oop.gui;

import agh.ics.oop.GrassField;
import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.out;


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
    private final int moveDelay = 10;
    private final int numOfAnimals = 10;
    private Scene scene;
    private Thread curThread1, curThread2;
    private SimulationEngine engine1, engine2;
    private final LineChart<String, Number> dataChart1 = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
    private final LineChart<String, Number> dataChart2 = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
    ScheduledExecutorService scheduledExecutorService2 = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService scheduledExecutorService1 = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void init() {
        this.vBoxGenerator.setAppObserver(this);
        this.map1 = new GrassField(10, 120, 120, 0.2F, false, 1, 30, true);
        this.map2 = new GrassField(10, 120, 120, 0.2F, true, 1, 30, true);

        this.engine1 = new SimulationEngine(this.map1, 100, 400, this.gridpane1, this.gridData1, this.moveDelay);
        this.engine1.addGuiObserver(this);
        this.curThread1 = new Thread(this.engine1);
        this.curThread1.setDaemon(true);

        this.engine2 = new SimulationEngine(this.map2, 100, 400, this.gridpane2, this.gridData2, this.moveDelay);
        this.engine2.addGuiObserver(this);
        this.curThread2 = new Thread(this.engine2);
        this.curThread2.setDaemon(true);
        this.curThread1.start();
        this.curThread2.start();
    }

    // for tracking clicked animal
    public void setTrackedAnimal(Animal animal){
        animal.setTrackedAnimal(animal);
        animal.getMap().setTrackedAnimal(animal);
        if (animal.getMap() == this.map1){
            this.trackedAnimal1 = animal;
            this.displayDetails(this.trackedAnimal1, this.animalDetails1);
        } else {
            this.trackedAnimal2 = animal;
            this.displayDetails(this.trackedAnimal2, this.animalDetails2);
        }
    }

    // info about magic condition being fullfilled and freezing simulation
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
//            long pre = System.nanoTime();
            ArrayList<Number> worldData = engine.getStatisticsData();
            gridpane.setGridLinesVisible(false);
            gridpane.getChildren().clear();
            gridpane.getColumnConstraints().clear();
            gridpane.getRowConstraints().clear();
            displayDetails(this.trackedAnimal1, this.animalDetails1);
            displayDetails(this.trackedAnimal2, this.animalDetails2);
            long pre = System.nanoTime();
            createGridPane(objects, newMap, gridpane);
            long post = System.nanoTime();
            fillData(engine, newMap, gridData, worldData);
//            long post = System.nanoTime();
            out.println("GUI " + (post-pre)/1000000);
        });
//        try {
//            Thread.sleep(this.moveDelay);
//        } catch (InterruptedException ex){
//            out.println(ex);
//        }
    }

    // generate text data about tracked animal
    public void displayDetails(Animal animal, VBox animalDetails){
        if (animal != null){
            animalDetails.getChildren().clear();
            Label info = new Label("Animal map: Borderless, " + "Animal tracked energy: " + animal.getEnergy());
            Label genes = new Label("Genom: " + animal.getGenes());
            Label children = new Label("Number of children: " + animal.getChildren());
            Label descendants = new Label("Number of descendants: " + animal.getDescendantsNumber());
            Label death = new Label("Year of death: " + animal.death);
            animalDetails.getChildren().addAll(info, genes, children, descendants);
            if (animal.death > 0){
                animalDetails.getChildren().add(death);
            }
        }
    }


    // generate text general info about map
    private void fillData(SimulationEngine engine, AbstractWorldMap newMap, VBox gridData, ArrayList<Number> data) {
        gridData.getChildren().clear();
        Label living = new Label("Number of animals: " + data.get(0));
        Label bushes = new Label("Number of grass: " + data.get(1));
        Label avEnergy = new Label("Average energy: " + data.get(2));
        Label avSpan = new Label("Average lifespan for dead: " + data.get(3));
        Label avChildren = new Label("Average number of children: " + data.get(4));
        gridData.getChildren().addAll(living, bushes, avSpan, avEnergy, avChildren);
        if (engine.getNumOfLiving() > 0){
            gridData.getChildren().add(new Label("Dominating genes: " + engine.getMostCommonGenes().toString()));
        }
    }

    // generate map
    private void createGridPane(ArrayList<MapObject> objects, AbstractWorldMap newMap, GridPane gridpane) {
        gridpane.setGridLinesVisible(true);
        Vector2d lowerLeft = newMap.lLeftGet();
        Vector2d upperRight = newMap.uRightGet();

        int width = upperRight.getCordX() - lowerLeft.getCordX() + 1;
        int height = upperRight.getCordY() - lowerLeft.getCordY() + 1;

        ColumnConstraints colConst = new ColumnConstraints(400/width);
        RowConstraints rowConst = new RowConstraints(400/height);

        for (int i = 0; i < width; i++){
            gridpane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < height; i++){
            gridpane.getRowConstraints().add(rowConst);
        }

        for (MapObject obj : objects){
            gridpane.add(this.vBoxGenerator.generateVBox(obj, newMap), obj.getPosition().getCordX() -
                    lowerLeft.getCordX(), upperRight.getCordY()-(obj.getPosition().getCordY()));
        }

    }

    // generate chart
    public void createDataPopulationChart(LineChart<String, Number> newChart, AbstractWorldMap map,
                                          ScheduledExecutorService executorService, SimulationEngine engine) {


        CategoryAxis generationAxis = (CategoryAxis) newChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) newChart.getYAxis();

        generationAxis.setMaxWidth(20);
        generationAxis.setLabel("Generation");
        yAxis.setLabel("Quantity");
        newChart.setTitle("Animal population");
        newChart.setAnimated(false);
        newChart.setMaxSize(500, 600);
        newChart.setMinSize(500, 100);
        newChart.setCreateSymbols(false);

        XYChart.Series<String, Number> animSeries = new XYChart.Series<>();
        animSeries.setName("Animals");
        XYChart.Series<String, Number> bushSeries = new XYChart.Series<>();
        bushSeries.setName("Bushes");
        XYChart.Series<String, Number> energySeries = new XYChart.Series<>();
        energySeries.setName("Average Energy");
        XYChart.Series<String, Number> spanSeries = new XYChart.Series<>();
        spanSeries.setName("Average Life Span");
        XYChart.Series<String, Number> childrenSeries = new XYChart.Series<>();
        childrenSeries.setName("Average Children count");
        newChart.getData().addAll(animSeries, bushSeries, energySeries, spanSeries, childrenSeries);

        ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList<>();
        seriesList.add(animSeries);
        seriesList.add(bushSeries);
        seriesList.add(energySeries);
        seriesList.add(spanSeries);
        seriesList.add(childrenSeries);

        executorService.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (engine.getRunning()) {
                    ArrayList<Number> data = engine.getStatisticsData();
                    animSeries.getData().add(new XYChart.Data<>(String.valueOf(map.getCurrentGen()),
                            data.get(0)));
                    bushSeries.getData().add(new XYChart.Data<>(String.valueOf(map.getCurrentGen()), data.get(1)));
                    energySeries.getData().add(new XYChart.Data<>(String.valueOf(map.getCurrentGen()),
                            data.get(2)));
                    spanSeries.getData().add(new XYChart.Data<>(String.valueOf(map.getCurrentGen()),
                            data.get(3)));
                    childrenSeries.getData().add(new XYChart.Data<>(String.valueOf(map.getCurrentGen()),
                            data.get(4)));

                    for (XYChart.Series<String, Number> series : seriesList){
                        if (series.getData().size() > 25) {
                            series.getData().remove(0);
                        }
                    }
                }
            });
        }, 0, this.moveDelay, TimeUnit.MILLISECONDS);



    }
    // generate pause buttons
    private void createPauseButton(BorderPane pane){
        Button pauseButton1 = new Button("Pause/Resume");
        Button pauseButton2 = new Button("Pause/Resume");
        pauseButton1.setOnAction(e -> {
            this.engine1.switchRunning();
            this.displayDetails(this.trackedAnimal1, this.animalDetails1);
            this.showGenes.setVisible(!this.engine1.getRunning());
            this.submitData1.setVisible(!this.engine1.getRunning());
        });
        pauseButton2.setOnAction(e -> {
            this.engine2.switchRunning();
            this.displayDetails(this.trackedAnimal2, this.animalDetails2);
            this.showGenes2.setVisible(!this.engine2.getRunning());
            this.submitData2.setVisible(!this.engine2.getRunning());
        });
        HBox buttons = new HBox();
        buttons.getChildren().addAll(pauseButton1, pauseButton2);
        buttons.setSpacing(30);
        buttons.setAlignment(Pos.CENTER);
        pane.setTop(buttons);
    }

    // generate paused gui and buttons
    private void createAnimalDetails(VBox container, Button sendData, VBox animDetails, Button showGenes,
                                     GridPane gridpane, SimulationEngine engine, AbstractWorldMap map){
        animDetails.setAlignment(Pos.CENTER);
        animDetails.setSpacing(10);


        showGenes.setOnAction(e -> {
            gridpane.setGridLinesVisible(false);
            gridpane.getChildren().clear();
            gridpane.getColumnConstraints().clear();
            gridpane.getRowConstraints().clear();
            createGridPane(engine.getAllWithGenes(engine.getMostCommonGenes()),
                    map, gridpane);
        });

        sendData.setOnAction(e -> {
            engine.writeDataToFile();
        });

        showGenes.setVisible(!engine.getRunning());
        sendData.setVisible(!engine.getRunning());
        container.getChildren().addAll(animDetails, showGenes, sendData);
    }

    public void start(Stage primaryStage){
        this.gridpane1.setAlignment(Pos.CENTER);
        this.gridpane2.setAlignment(Pos.CENTER);

        createDataPopulationChart(this.dataChart1, this.map1, this.scheduledExecutorService1, this.engine1);
        createDataPopulationChart(this.dataChart2, this.map2, this.scheduledExecutorService2, this.engine2);
        BorderPane mainPane = new BorderPane();

        VBox layoutFirst = new VBox();
        layoutFirst.setSpacing(10);
        layoutFirst.getChildren().addAll(this.gridpane1, this.gridData1, this.dataChart1);
        layoutFirst.setAlignment(Pos.CENTER);

        VBox layoutSecond = new VBox();
        layoutSecond.setSpacing(10);
        layoutSecond.getChildren().addAll(this.gridpane2, this.gridData2, this.dataChart2);
        layoutSecond.setAlignment(Pos.CENTER);

        mainPane.setLeft(layoutFirst);
        mainPane.setRight(layoutSecond);
        this.createPauseButton(mainPane);

        VBox animDetails = new VBox();
        this.createAnimalDetails(animDetails, this.submitData1, this.animalDetails1, this.showGenes, this.gridpane1,
                this.engine1, this.map1);
        this.createAnimalDetails(animDetails, this.submitData2, this.animalDetails2, this.showGenes2, this.gridpane2,
                this.engine2, this.map2);

        animDetails.setAlignment(Pos.CENTER);
        animDetails.setSpacing(20);
        mainPane.setCenter(animDetails);

        Scene scene = new Scene(mainPane, 1400, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        scheduledExecutorService1.shutdownNow();
        scheduledExecutorService2.shutdownNow();
    }

}
