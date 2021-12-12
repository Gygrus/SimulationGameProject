package agh.ics.oop;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

import static java.lang.System.out;

public class GrassField extends AbstractWorldMap {
    private final int numOfBushes;
    public GrassField(int n, int width, int height, float jungleRatio, boolean borders, int energyLoss) {
        this.energyLoss = energyLoss;
        this.numOfBushes = n;
        this.borders = borders;
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width, height);
        this.width = width;
        this.height = height;
        generateBushes();
        setJunglePositions(jungleRatio);
    }

    private void setJunglePositions(float jungleRatio) {
        int area = (int) (jungleRatio* (float) (this.width*this.height));
        int divider = 1;
        while (divider < 2 && area >= 4){
            for (int i = 2; i < Math.sqrt(area)+1; i++){
                if (area%i == 0){
                    divider = i;
                }
            }
            area -= 1;
        }
        out.println(divider);
        area += 1;
        int jWidth = divider;
        int jHeight = area/divider;
        int startleft = (this.width-jWidth)/2;
        int startdown = (this.height-jHeight)/2;
        this.jungleLowerLeft = new Vector2d(startleft, startdown);
        this.jungleUpperRight = new Vector2d(startleft + jWidth, startdown + jHeight);
        out.println(this.jungleLowerLeft);
        out.println(this.jungleUpperRight);
    }

    private void generateBushes() {
        int i = 0;
        int curRandom_x, curRandom_y;
        Vector2d curVec;
        while (i < this.numOfBushes) {
            curRandom_x = ThreadLocalRandom.current().nextInt(0, (int) Math.sqrt(this.numOfBushes * 10) + 1);
            curRandom_y = ThreadLocalRandom.current().nextInt(0, (int) Math.sqrt(this.numOfBushes * 10) + 1);
            curVec = new Vector2d(curRandom_x, curRandom_y);
            if (!this.bushes.containsKey(curVec) && !this.animals.containsKey(curVec)) {
                this.bushes.put(curVec, new Grass(curVec));
                i += 1;
            }
        }
    }


    @Override public HashMap<Vector2d, Grass> getBushes() {
        return this.bushes;
    }

//    @Override
//    public Vector2d lLeftGet() {
//
//        return this.boundaryData.getLowerLeft();
//    }

//    @Override
//    public Vector2d uRightGet() {
//
//        return this.boundaryData.getUpperRight();
//    }

//    @Override
//    public boolean isOccupied(Vector2d position) {
//        return super.isOccupied(position) || this.bushes.containsKey(position);
//    }


//    @Override
//    public Object objectAt(Vector2d position) {
//        Object obj = super.objectAt(position);
//        if (obj != null) {
//            return obj;
//        }
//        return this.bushes.get(position);
//    }

//    @Override
//    public boolean place(Animal animal) {
//        if (super.place(animal)){
//            this.boundaryData.addAnimal(animal.getPosition());
//            animal.addObserver(this.boundaryData);
//            return true;
//        }
//        return false;
//    }
}
