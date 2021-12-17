package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationGuiMaker {
    private GuiElementBox vBoxGenerator;
    private int moveDelay;

    public SimulationGuiMaker(GuiElementBox vBoxGenerator, int moveDelay){
        this.moveDelay = moveDelay;
        this.vBoxGenerator = vBoxGenerator;
    }

    // manage layouts
    public void positionLayouts(VBox layout, GridPane gridpane, VBox gridData,LineChart<String, Number> dataChart){
        layout.setSpacing(10);
        layout.getChildren().addAll(gridpane, gridData, dataChart);
        layout.setAlignment(Pos.CENTER);
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
    public void fillData(SimulationEngine engine, AbstractWorldMap newMap, VBox gridData, ArrayList<Number> data) {
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
    public void createGridPane(ArrayList<MapObject> objects, AbstractWorldMap newMap, GridPane gridpane) {
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
        }, 0, engine.getMoveDelay(), TimeUnit.MILLISECONDS);



    }

    // generate paused gui and buttons
    public void createAnimalDetails(VBox container, Button sendData, VBox animDetails, Button showGenes,
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
}
