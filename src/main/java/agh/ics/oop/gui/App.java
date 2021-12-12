package agh.ics.oop.gui;

//import agh.ics.oop.GrassField;
import agh.ics.oop.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import static java.lang.System.out;


//public class App extends Application {
//    private AbstractWorldMap map;
//    private GridPane gridpane = new GridPane();
//    private GuiElementBox vBoxGenerator = new GuiElementBox();
//    private int moveDelay = 300;
//    private int numOfAnimals = 10;
//    private Scene scene;
//    private Thread thread;
//    private SimulationEngine engine;
//
//    @Override
//    public void init() {
//        String[] args = {"f", "b", "r", "l", "f", "f", "r", "r", "f", "f", "f", "f", "f", "f", "f", "f"};
////        String[] args = getParameters().getRaw().toArray(new String[0]);
//        MoveDirection[] directions = new OptionsParser().parse(args);
//        this.map = new GrassField(10);
////        this.map = new RectangularMap(10, 16);
//        Vector2d[] positions = {new Vector2d(2, 2), new Vector2d(3, 4)};
//        this.engine = new SimulationEngine(this.map, positions);
//        this.engine.addGuiObserver(this);
//    }
//
//
//    public void guiUpdate(AbstractWorldMap newMap){
//        Platform.runLater(()->{
//            this.gridpane.setGridLinesVisible(false);
//            this.gridpane.getChildren().clear();
//            this.gridpane.getColumnConstraints().clear();
//            this.gridpane.getRowConstraints().clear();
//            createGridPane(newMap);
//        });
//        try {
//            Thread.sleep(this.moveDelay);
//        } catch (InterruptedException ex){
//            out.println(ex);
//        }
//    }
//
//
//
//    private void createGridPane(AbstractWorldMap newMap) {
//        this.gridpane.setGridLinesVisible(true);
//        Vector2d lowerLeft = newMap.lLeftGet();
//        Vector2d upperRight = newMap.uRightGet();
//
//        int width = upperRight.getCordX() - lowerLeft.getCordX() + 2;
//        int height = upperRight.getCordY() - lowerLeft.getCordY() + 2;
//
//        ColumnConstraints colConst = new ColumnConstraints(35);
//        RowConstraints rowConst = new RowConstraints(35);
//
//        for (int i = 0; i < width; i++){
//            this.gridpane.getColumnConstraints().add(colConst);
//        }
//
//        for (int i = 0; i < height; i++){
//            this.gridpane.getRowConstraints().add(rowConst);
//        }
//
//        Label first = new Label("y/x");
//        this.gridpane.add(first, 0, 0);
//        this.gridpane.setHalignment(first, HPos.CENTER);
//
//        for (int i = 0; i < height-1; i++){
//            Label label = new Label(String.valueOf(upperRight.getCordY()-i));
//            this.gridpane.add(label, 0, i+1);
//            this.gridpane.setHalignment(label, HPos.CENTER);
//        }
//
//        for (int i = 0; i < width-1; i++){
//            Label label = new Label(String.valueOf(lowerLeft.getCordX()+i));
//            this.gridpane.add(label, i+1, 0);
//            this.gridpane.setHalignment(label, HPos.CENTER);
//        }
//
//        for (MapObject animal : newMap.getAnimals().values()){
//            this.gridpane.add(this.vBoxGenerator.generateVBox(animal), animal.getPosition().getCordX() - lowerLeft.getCordX() + 1, upperRight.getCordY()-(animal.getPosition().getCordY())+1);
//
//        }
//
//        for (MapObject bush : newMap.getBushes().values()){
//            this.gridpane.add(this.vBoxGenerator.generateVBox(bush), bush.getPosition().getCordX() - lowerLeft.getCordX() + 1, upperRight.getCordY()-(bush.getPosition().getCordY())+1);
//        }
//
//
//        addOtherGUI(width, height);
//
//
//    }
//
//    private void addOtherGUI(int width, int height) {
//        HBox inputStart = new HBox();
//        TextField inputValue = new TextField();
//        Button simStart = new Button("Start");
//        simStart.setOnAction(e -> {
//            MoveDirection[] directions = new OptionsParser().parse(inputValue.getText().split(" "));
//            this.engine.setDirections(directions);
//            Thread curThread = new Thread(this.engine);
//            curThread.setDaemon(true);
//            curThread.start();
//        });
//        inputStart.getChildren().addAll(inputValue, simStart);
//        this.gridpane.add(inputStart, width+3, 0, 2, 3);
//    }
//
//    public void start(Stage primaryStage){
//        createGridPane(this.map);
//        Scene scene = new Scene(this.gridpane, 800, 1000);
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//    }
//}
