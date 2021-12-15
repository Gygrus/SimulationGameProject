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
    private GridPane gridpane1 = new GridPane();
    private GridPane gridpane2 = new GridPane();
    private VBox gridData1 = new VBox();
    private VBox gridData2 = new VBox();
    private VBox animalDetails1 = new VBox();
    private VBox animalDetails2 = new VBox();
    private GuiElementBox vBoxGenerator = new GuiElementBox();
    private int moveDelay = 60;
    private int numOfAnimals = 10;
    private Scene scene;
    private Thread curThread1, curThread2;
    private SimulationEngine engine1, engine2;
    private LineChart<String, Number> dataChart1 = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
    private LineChart<String, Number> dataChart2 = new LineChart<String, Number>(new CategoryAxis(), new NumberAxis());
    ScheduledExecutorService scheduledExecutorService2 = Executors.newSingleThreadScheduledExecutor();
    ScheduledExecutorService scheduledExecutorService1 = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void init() {
        this.map1 = new GrassField(10, 10, 10, 0.3F, false, 1, 50);
        this.map2 = new GrassField(10, 10, 10, 0.4F, true, 1, 50);

        this.engine1 = new SimulationEngine(this.map1, 10, 4000, this.gridpane1, this.gridData1);
        this.engine1.addGuiObserver(this);
        this.curThread1 = new Thread(this.engine1);
        this.curThread1.setDaemon(true);

        this.engine2 = new SimulationEngine(this.map2, 100, 4000, this.gridpane2, this.gridData2);
        this.engine2.addGuiObserver(this);
        this.curThread2 = new Thread(this.engine2);
        this.curThread2.setDaemon(true);
        this.curThread1.start();
        this.curThread2.start();
    }


    public void guiUpdate(ArrayList<MapObject> objects, AbstractWorldMap newMap, SimulationEngine engine, GridPane gridpane, VBox gridData){
        Platform.runLater(()->{
            gridpane.setGridLinesVisible(false);
            gridpane.getChildren().clear();
            gridpane.getColumnConstraints().clear();
            gridpane.getRowConstraints().clear();
            createGridPane(objects, newMap, gridpane);
            fillData(engine, newMap, gridData);
        });
        try {
            Thread.sleep(this.moveDelay);
        } catch (InterruptedException ex){
            out.println(ex);
        }
    }



    private void fillData(SimulationEngine engine, AbstractWorldMap newMap, VBox gridData) {
        gridData.getChildren().clear();
        Label living = new Label("Number of animals: " + engine.getNumOfLiving());
        Label bushes = new Label("Number of grass: " + newMap.numOfBushes);
        Label genes = new Label("Dominating genes: " + engine.getMostCommonGenes());
        Label avEnergy = new Label("Average energy: " + engine.getAverageEnergy());
        Label avSpan = new Label("Average lifespan for dead: " + engine.getAverageDeadLifeSpan());
        Label avChildren = new Label("Average number of children: " + engine.getAverageChildren());
        gridData.getChildren().addAll(living, bushes, avSpan, genes, avEnergy, avChildren);
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
            gridpane.add(this.vBoxGenerator.generateVBox(obj), obj.getPosition().getCordX() - lowerLeft.getCordX(), upperRight.getCordY()-(obj.getPosition().getCordY()));
        }

    }


    public void createDataPopulationChart(LineChart<String, Number> newChart, AbstractWorldMap map, ScheduledExecutorService executorService, SimulationEngine engine) {
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



        executorService.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                if (engine.getRunning()) {
                    animSeries.getData().add(new XYChart.Data<>(String.valueOf(map.currentGen), engine.getNumOfLiving()));
                    bushSeries.getData().add(new XYChart.Data<>(String.valueOf(map.currentGen), map.numOfBushes));
                    energySeries.getData().add(new XYChart.Data<>(String.valueOf(map.currentGen), engine.getAverageEnergy()));
                    spanSeries.getData().add(new XYChart.Data<>(String.valueOf(map.currentGen), engine.getAverageDeadLifeSpan()));
                    childrenSeries.getData().add(new XYChart.Data<>(String.valueOf(map.currentGen), engine.getAverageChildren()));
                    if (animSeries.getData().size() > 25) {
                        animSeries.getData().remove(0);
                    }
                    if (bushSeries.getData().size() > 25) {
                        bushSeries.getData().remove(0);
                    }
                    if (energySeries.getData().size() > 25) {
                        energySeries.getData().remove(0);
                    }
                    if (spanSeries.getData().size() > 25) {
                        spanSeries.getData().remove(0);
                    }
                    if (childrenSeries.getData().size() > 25) {
                        childrenSeries.getData().remove(0);
                    }
                }
            });
        }, 0, this.moveDelay, TimeUnit.MILLISECONDS);



    }

    private void createPauseButton(BorderPane pane){
        Button pauseButton1 = new Button("Pause/Resume");
        Button pauseButton2 = new Button("Pause/Resume");
        pauseButton1.setOnAction(e -> {
            this.engine1.switchRunning();
        });
        pauseButton2.setOnAction(e -> {
            this.engine2.switchRunning();
        });
        HBox buttons = new HBox();
        buttons.getChildren().addAll(pauseButton1, pauseButton2);
        buttons.setSpacing(30);
        buttons.setAlignment(Pos.CENTER);
        pane.setTop(buttons);
    }

    public void start(Stage primaryStage){
//        createGridPane(this.map1);
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
