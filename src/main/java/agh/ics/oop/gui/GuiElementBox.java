package agh.ics.oop.gui;
import agh.ics.oop.AbstractWorldMap;
import agh.ics.oop.Animal;
import agh.ics.oop.Grass;
import agh.ics.oop.MapObject;
import com.sun.javafx.geom.RectangularShape;
import com.sun.javafx.geom.Shape;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;

import static java.lang.System.out;


import java.io.FileInputStream;

public class GuiElementBox {
    public App appObserver;
    Image upImage;
    Image downImage;
    Image leftImage;
    Image rightImage;
    Image grassImage;



    public void setAppObserver(App observer){
        this.appObserver = observer;
    }


    public GuiElementBox(){
        try {
            this.upImage = new Image(new FileInputStream("src/main/resources/up.png"));
            this.downImage = new Image(new FileInputStream("src/main/resources/down.png"));
            this.rightImage = new Image(new FileInputStream("src/main/resources/right.png"));
            this.leftImage = new Image(new FileInputStream("src/main/resources/left.png"));
            this.grassImage = new Image(new FileInputStream("src/main/resources/grass.png"));
        }
        catch (Exception FileNotFoundException) {
            out.println(FileNotFoundException);
        }
    }

    public VBox generateVBox(MapObject object, AbstractWorldMap newMap){
//        Label text = new Label(object.getLabel());
//        ImageView newView = new ImageView();
//        switch (object.getInputStream()){
//            case "src/main/resources/up.png" -> newView = new ImageView(this.upImage);
//            case "src/main/resources/down.png" -> newView = new ImageView(this.downImage);
//            case "src/main/resources/right.png" -> newView = new ImageView(this.rightImage);
//            case "src/main/resources/left.png" -> newView = new ImageView(this.leftImage);
//            case "src/main/resources/grass.png" -> newView = new ImageView(this.grassImage);
//            default -> throw new IllegalStateException("Unexpected value");
//        };
        VBox vBox;
        if (object instanceof Animal){
            Ellipse newView = new Ellipse((float) 400 / (newMap.getWidth()*2), (float) 400 / (newMap.getHeight()*2));
            newView.setCenterX(50.0f);
            newView.setCenterY(50.0f);
            newView.setFill(Color.BLUE);
            vBox = new VBox(newView);
        } else {
            Rectangle newView = new Rectangle((float) 400 / (newMap.getHeight()*2), (float) 400 / (newMap.getWidth()*2));
//            newView.setCenterX(50.0f);
//            newView.setCenterY(50.0f);
            newView.setFill(Color.GREEN);
            vBox = new VBox(newView);
        }
//        newView.setFitHeight(20);
//        newView.setFitWidth(20);
        vBox.setAlignment(Pos.CENTER);

        EventHandler<MouseEvent> eventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                if (object instanceof Animal){
                    Animal animal = (Animal) object;
                    sendAnimal(animal);
                }
            }
        };
        vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        return vBox;
    }

    public void sendAnimal(Animal animal){
        this.appObserver.setTrackedAnimal(animal);
    }


}
