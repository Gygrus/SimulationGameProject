package agh.ics.oop.gui;

import agh.ics.oop.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulationGuiMaker {
    private final GuiElementBox vBoxGenerator;

    public SimulationGuiMaker(GuiElementBox vBoxGenerator){
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
            animalDetails.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            Label info = new Label("Animal tracked energy: " + animal.getEnergy());
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
    public void fillData(SimulationEngine engine, VBox gridData, ArrayList<Number> data) {
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

    // generate map key
    public void createSymbolInfo(VBox container){
        HBox infoBar1 = new HBox();
        Ellipse energySymbol1 = new Ellipse(5, 5);
        energySymbol1.setFill(Color.DEEPPINK);
        Label symbolCaption1 = new Label(" - currently tracked animal");
        infoBar1.getChildren().addAll(energySymbol1, symbolCaption1);
        infoBar1.setAlignment(Pos.CENTER);

        HBox infoBar2 = new HBox();
        Ellipse energySymbol2 = new Ellipse(5, 5);
        energySymbol2.setFill(Color.BLUE);
        Label symbolCaption2 = new Label(" - animal with no less than 80% starting energy");
        infoBar2.getChildren().addAll(energySymbol2, symbolCaption2);
        infoBar2.setAlignment(Pos.CENTER);

        HBox infoBar3 = new HBox();
        Ellipse energySymbol3 = new Ellipse(5, 5);
        energySymbol3.setFill(Color.YELLOW);
        Label symbolCaption3 = new Label(" - animal with no less than 50% starting energy");
        infoBar3.getChildren().addAll(energySymbol3, symbolCaption3);
        infoBar3.setAlignment(Pos.CENTER);

        HBox infoBar4 = new HBox();
        Ellipse energySymbol4 = new Ellipse(5, 5);
        energySymbol4.setFill(Color.ORANGE);
        Label symbolCaption4 = new Label(" - animal with no less than 30% starting energy");
        infoBar4.getChildren().addAll(energySymbol4, symbolCaption4);
        infoBar4.setAlignment(Pos.CENTER);

        HBox infoBar5 = new HBox();
        Ellipse energySymbol5 = new Ellipse(5, 5);
        energySymbol5.setFill(Color.RED);
        Label symbolCaption5 = new Label(" - animal with no less than 50% starting energy");
        infoBar5.getChildren().addAll(energySymbol5, symbolCaption5);
        infoBar5.setAlignment(Pos.CENTER);

        HBox infoBar6 = new HBox();
        Ellipse energySymbol6 = new Ellipse(5, 5);
        energySymbol6.setFill(Color.BLACK);
        Label symbolCaption6 = new Label(" - animal with less than 60% starting energy");
        infoBar6.getChildren().addAll(energySymbol6, symbolCaption6);
        infoBar6.setAlignment(Pos.CENTER);

        HBox infoBar7 = new HBox();
        Rectangle energySymbol7 = new Rectangle(5, 5);
        energySymbol7.setFill(Color.LIGHTGREEN);
        Label symbolCaption7 = new Label(" - grass on savanna field");
        infoBar7.getChildren().addAll(energySymbol7, symbolCaption7);
        infoBar7.setAlignment(Pos.CENTER);

        HBox infoBar8 = new HBox();
        Rectangle energySymbol8 = new Rectangle(5, 5);
        energySymbol8.setFill(Color.GREEN);
        Label symbolCaption8 = new Label(" - grass on jungle field");
        infoBar8.getChildren().addAll(energySymbol8, symbolCaption8);
        infoBar8.setAlignment(Pos.CENTER);

        container.getChildren().addAll(infoBar1, infoBar2, infoBar3, infoBar4, infoBar5, infoBar6, infoBar7, infoBar8);
    }
}
