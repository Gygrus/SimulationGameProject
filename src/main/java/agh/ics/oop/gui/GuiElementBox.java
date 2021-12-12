package agh.ics.oop.gui;
import agh.ics.oop.MapObject;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import static java.lang.System.out;

import java.io.FileInputStream;

public class GuiElementBox {
    Image upImage;
    Image downImage;
    Image leftImage;
    Image rightImage;
    Image grassImage;


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

    public VBox generateVBox(MapObject object){
        Label text = new Label(object.getLabel());
        ImageView newView;
        switch (object.getInputStream()){
            case "src/main/resources/up.png" -> newView = new ImageView(this.upImage);
            case "src/main/resources/down.png" -> newView = new ImageView(this.downImage);
            case "src/main/resources/right.png" -> newView = new ImageView(this.rightImage);
            case "src/main/resources/left.png" -> newView = new ImageView(this.leftImage);
            case "src/main/resources/grass.png" -> newView = new ImageView(this.grassImage);
            default -> throw new IllegalStateException("Unexpected value");
        };
        newView.setFitHeight(20);
        newView.setFitWidth(20);
        VBox vBox = new VBox(newView, text);
        vBox.setAlignment(Pos.CENTER);
        return vBox;
    }


}
