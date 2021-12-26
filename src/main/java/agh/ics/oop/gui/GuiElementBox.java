package agh.ics.oop.gui;
import agh.ics.oop.AbstractWorldMap;
import agh.ics.oop.Animal;
import agh.ics.oop.MapObject;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;


public class GuiElementBox {
    public App appObserver;

    public void setAppObserver(App observer){
        this.appObserver = observer;
    }

    public VBox generateVBox(MapObject object, AbstractWorldMap newMap){
        VBox vBox;
        if (object instanceof Animal){
            Ellipse newView = new Ellipse((float) 400 / (newMap.getWidth()*2.5), (float) 400 / (newMap.getHeight()*2.5));
            newView.setCenterX(50.0f);
            newView.setCenterY(50.0f);
            Animal animal = (Animal) object;
            if (animal.getTrackedAnimal() == animal){
                newView.setFill(Color.DEEPPINK);
            } else if (animal.getEnergy() >= (float) 0.8*animal.getMap().getEngineObserver().initialEnergy){
                newView.setFill(Color.BLUE);
            } else if (animal.getEnergy() >= (float) 0.5*animal.getMap().getEngineObserver().initialEnergy){
                newView.setFill(Color.YELLOW);
            } else if (animal.getEnergy() >= (float) 0.3*animal.getMap().getEngineObserver().initialEnergy){
                newView.setFill(Color.ORANGE);
            } else if (animal.getEnergy() >= (float) 0.1*animal.getMap().getEngineObserver().initialEnergy) {
                newView.setFill(Color.RED);
            } else {
                newView.setFill(Color.BLACK);
            }
            vBox = new VBox(newView);
        } else {
            Rectangle newView = new Rectangle((float) 400 / (newMap.getWidth()*(1.1)), (float) 400 / (newMap.getHeight()*(1.1)));
            if (newMap.checkIfInJungle(object.getPosition())){
                newView.setFill(Color.GREEN);
            } else {
                newView.setFill(Color.LIGHTGREEN);
            }
            vBox = new VBox(newView);
        }
        vBox.setAlignment(Pos.CENTER);

        EventHandler<MouseEvent> eventHandler = e -> {
            if (object instanceof Animal){
                Animal animal = (Animal) object;
                sendAnimal(animal);
            }
        };
        vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);
        return vBox;
    }

    public void sendAnimal(Animal animal){
        this.appObserver.setTrackedAnimal(animal);
    }


}
