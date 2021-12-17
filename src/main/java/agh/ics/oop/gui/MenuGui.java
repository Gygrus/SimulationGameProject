package agh.ics.oop.gui;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.util.function.UnaryOperator;

public class MenuGui {
    private App appObserver;
    private TextField animalNumber1 = new TextField("10"), animalNumber2 = new TextField("10");
    private TextField map1Width = new TextField("10"), map2Width = new TextField("10");
    private TextField map1Height = new TextField("10"), map2Height = new TextField("10");
    private TextField startEnergy1 = new TextField("10"), startEnergy2 = new TextField("10");
    private TextField moveEnergy1 = new TextField("10"), moveEnergy2 = new TextField("10");
    private TextField plantEnergy1 = new TextField("10"), plantEnergy2 = new TextField("10");
    private TextField jungleRatio1 = new TextField("0.1"), jungleRatio2 = new TextField("0.1");
    private CheckBox isMagic1 = new CheckBox("Magic constraint!"), isMagic2 = new CheckBox("Magic constraint!");

    public MenuGui (App observer) {
        this.appObserver = observer;
    }

    public Scene generateScene(){
        VBox mainPanel = new VBox();
        HBox container = new HBox();
        VBox borderLessMap = new VBox();
        VBox bordersMap = new VBox();

        Label mainTitle = new Label("Settings");

        borderLessMap.setSpacing(10);
        borderLessMap.setAlignment(Pos.TOP_CENTER);
        bordersMap.setSpacing(10);
        bordersMap.setAlignment(Pos.TOP_CENTER);
        container.setAlignment(Pos.CENTER);
        container.setSpacing(20);

        fillWithOptions(borderLessMap, 1);

        fillWithOptions(bordersMap, 2);

        container.getChildren().addAll(borderLessMap, bordersMap);

        Button submit = generateSubmitButton();
        mainPanel.setAlignment(Pos.CENTER);
        mainPanel.getChildren().addAll(mainTitle, container, submit);

        Scene menuScene = new Scene(mainPanel, 600, 350);
        return menuScene;
    }

    private void fillWithOptions(VBox mapOptions, int maptype){

        // kod ze strony https://edencoding.com/javafx-textfield/
        UnaryOperator<TextFormatter.Change> numberValidationFormatter = change -> {
            if(change.getText().matches("\\d+")){
                return change; //if change is a number
            } else {
                change.setText(""); //else make no change
                return change;
            }
        };

        UnaryOperator<TextFormatter.Change> doubleValidationFormatter = change -> {
            if(change.getText().matches("\\d+") || change.getText().matches(".")){
                return change; //if change is a number
            } else {
                change.setText(""); //else make no change
                return change;
            }
        };

        HBox widthBox = new HBox();
        HBox heightBox = new HBox();
        HBox anNumBox = new HBox();
        HBox startEnBox = new HBox();
        HBox moveEnBox = new HBox();
        HBox plantEnBox = new HBox();
        HBox jRatBox = new HBox();

        Label widthCap = new Label("Width");
        Label heightCap = new Label("Height");
        Label anNumCap = new Label("Starting animal number");
        Label startEnCap = new Label("Starting energy");
        Label moveEnCap = new Label("Move energy");
        Label plantEnCap = new Label("Plant energy");
        Label jRatCap = new Label("Jungle ratio");

        if (maptype == 1) {
            Label title = new Label("Options for borderless map");

            this.map1Width = new TextField("10");
            this.map1Height = new TextField("10");
            this.animalNumber1 = new TextField("10");
            this.startEnergy1 = new TextField("10");
            this.moveEnergy1 = new TextField("10");
            this.plantEnergy1 = new TextField("10");
            this.jungleRatio1 = new TextField("0.1");
            this.isMagic1 = new CheckBox("Magic condition!");

            this.map1Width.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.map1Height.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.animalNumber1.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.startEnergy1.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.moveEnergy1.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.plantEnergy1.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.jungleRatio1.setTextFormatter(new TextFormatter<String>(doubleValidationFormatter));

            widthBox.getChildren().addAll(widthCap, this.map1Width);
            heightBox.getChildren().addAll(heightCap, this.map1Height);
            anNumBox.getChildren().addAll(anNumCap, this.animalNumber1);
            startEnBox.getChildren().addAll(startEnCap, this.startEnergy1);
            moveEnBox.getChildren().addAll(moveEnCap, this.moveEnergy1);
            plantEnBox.getChildren().addAll(plantEnCap, this.plantEnergy1);
            jRatBox.getChildren().addAll(jRatCap, this.jungleRatio1);

            widthBox.setSpacing(10);
            heightBox.setSpacing(10);
            anNumBox.setSpacing(10);
            startEnBox.setSpacing(10);
            moveEnBox.setSpacing(10);
            plantEnBox.setSpacing(10);
            jRatBox.setSpacing(10);

            mapOptions.getChildren().addAll(title, widthBox, heightBox, anNumBox, startEnBox, moveEnBox,
                    plantEnBox, jRatBox, this.isMagic1);
        } else {
            Label title = new Label("Options for borderless map");

            this.map2Width = new TextField("10");
            this.map2Height = new TextField("10");
            this.animalNumber2 = new TextField("10");
            this.startEnergy2 = new TextField("10");
            this.moveEnergy2 = new TextField("10");
            this.plantEnergy2 = new TextField("10");
            this.jungleRatio2 = new TextField("0.1");
            this.isMagic2 = new CheckBox("Magic condition!");

            this.map2Width.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.map2Height.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.animalNumber2.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.startEnergy2.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.moveEnergy2.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.plantEnergy2.setTextFormatter(new TextFormatter<String>(numberValidationFormatter));
            this.jungleRatio2.setTextFormatter(new TextFormatter<String>(doubleValidationFormatter));

            widthBox.getChildren().addAll(widthCap, this.map2Width);
            heightBox.getChildren().addAll(heightCap, this.map2Height);
            anNumBox.getChildren().addAll(anNumCap, this.animalNumber2);
            startEnBox.getChildren().addAll(startEnCap, this.startEnergy2);
            moveEnBox.getChildren().addAll(moveEnCap, this.moveEnergy2);
            plantEnBox.getChildren().addAll(plantEnCap, this.plantEnergy2);
            jRatBox.getChildren().addAll(jRatCap, this.jungleRatio2);

            widthBox.setSpacing(10);
            heightBox.setSpacing(10);
            anNumBox.setSpacing(10);
            startEnBox.setSpacing(10);
            moveEnBox.setSpacing(10);
            plantEnBox.setSpacing(10);
            jRatBox.setSpacing(10);

            mapOptions.getChildren().addAll(title, widthBox, heightBox, anNumBox, startEnBox, moveEnBox,
                    plantEnBox, jRatBox, this.isMagic2);
        }
    }

    private Button generateSubmitButton() {
        Button submit = new Button("Submit settings and start simulation!");
        submit.setOnAction(e -> {
            if (
                    Integer.parseInt(this.animalNumber1.getText()) > 0 &&
                    Integer.parseInt(this.animalNumber2.getText()) > 0 &&
                    Integer.parseInt(this.map1Width.getText()) > 0 &&
                    Integer.parseInt(this.map2Width.getText()) > 0 &&
                    Integer.parseInt(this.map1Height.getText()) > 0 &&
                    Integer.parseInt(this.map2Height.getText()) > 0 &&
                    Integer.parseInt(this.startEnergy1.getText()) > 0 &&
                    Integer.parseInt(this.startEnergy2.getText()) > 0 &&
                    Integer.parseInt(this.moveEnergy1.getText()) > 0 &&
                    Integer.parseInt(this.moveEnergy2.getText()) > 0 &&
                    Integer.parseInt(this.plantEnergy1.getText()) > 0 &&
                    Integer.parseInt(this.plantEnergy2.getText()) > 0 &&
                    this.jungleRatio1.getText().matches("\\d+\\.\\d+") &&
                    Float.parseFloat(this.jungleRatio1.getText()) > 0 &&
                    Float.parseFloat(this.jungleRatio1.getText()) <= 1 &&
                    this.jungleRatio2.getText().matches("\\d+\\.\\d+") &&
                    Float.parseFloat(this.jungleRatio2.getText()) > 0 &&
                    Float.parseFloat(this.jungleRatio2.getText()) <= 1){

                this.appObserver.animalNumber1 = Integer.parseInt(this.animalNumber1.getText());
                this.appObserver.animalNumber2 = Integer.parseInt(this.animalNumber2.getText());
                this.appObserver.map1Width = Integer.parseInt(this.map1Width.getText());
                this.appObserver.map2Width = Integer.parseInt(this.map2Width.getText());
                this.appObserver.map1Height = Integer.parseInt(this.map1Height.getText());
                this.appObserver.map2Height = Integer.parseInt(this.map2Height.getText());
                this.appObserver.startEnergy1 = Integer.parseInt(this.startEnergy1.getText());
                this.appObserver.startEnergy2 = Integer.parseInt(this.startEnergy2.getText());
                this.appObserver.moveEnergy1 = Integer.parseInt(this.moveEnergy1.getText());
                this.appObserver.moveEnergy2 = Integer.parseInt(this.moveEnergy2.getText());
                this.appObserver.plantEnergy1 = Integer.parseInt(this.plantEnergy1.getText());
                this.appObserver.plantEnergy2 = Integer.parseInt(this.plantEnergy2.getText());
                this.appObserver.jungleRatio1 = Float.parseFloat(this.jungleRatio1.getText());
                this.appObserver.jungleRatio2 = Float.parseFloat(this.jungleRatio2.getText());
                this.appObserver.isMagic1 = this.isMagic1.isSelected();
                this.appObserver.isMagic2 = this.isMagic2.isSelected();

                this.appObserver.showSimulation();
            }
        });
        return submit;
    }



}
