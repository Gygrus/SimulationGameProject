package agh.ics.oop;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;

import static java.lang.System.out;

public class GrassField extends AbstractWorldMap {
    private final int initialNumOfBushes;
    public GrassField(int n, int width, int height, float jungleRatio, boolean borders, int energyLoss, int bushEnergy) {
        this.bushEnergy = bushEnergy;
        this.energyLoss = energyLoss;
        this.initialNumOfBushes = n;
        this.borders = borders;
        this.lowerLeft = new Vector2d(0, 0);
        this.upperRight = new Vector2d(width, height);
        this.width = width;
        this.height = height;
        setJunglePositions(jungleRatio);
        generateBushesPosition();
//        out.println(this.bushesJungle);
//        out.println(this.bushesSavanna);
//        out.println(this.bushesAll);
        generateInitialBushes();

    }

    private void setJunglePositions(float jungleRatio) {
        int area = (int) (jungleRatio* (float) (this.width*this.height));
        int divider = 1;
        while (divider < 2 && area >= 4){
            for (int i = (int) Math.sqrt(area); i >= 2; i--){
                if (area%i == 0 && (Math.max(area/i, i) <= Math.max(this.width, this.height)) && (Math.min(area/i, i) <= Math.min(this.width, this.height))){
                    divider = i;
                    break;
                }
            }
            area -= 1;
        }
//        out.println(divider);
        area += 1;
        int jWidth = divider;
        int jHeight = area/divider;
        if ((jWidth > jHeight && this.getWidth() < this.getHeight()) || (jWidth < jHeight && this.getWidth() > this.getHeight())){
            int temp = jWidth;
            jWidth = jHeight;
            jHeight = temp;
        }
        int startleft = (this.width-jWidth)/2;
        int startdown = (this.height-jHeight)/2;
        this.jungleLowerLeft = new Vector2d(startleft, startdown);
        this.jungleUpperRight = new Vector2d(startleft + jWidth, startdown + jHeight);
    }

    private void generateInitialBushes() {
        for (int i = 0; i < Math.min(this.initialNumOfBushes, (this.getWidth()+1)*(this.getHeight())); i++) {
            int randIndex = ThreadLocalRandom.current().nextInt(0, this.bushesAll.size());
            Vector2d curVec = this.bushesAll.get(randIndex);
            this.bushes.put(curVec, new Grass(curVec));
            this.bushesAll.remove(curVec);
            this.bushesJungle.remove(curVec);
            this.bushesSavanna.remove(curVec);
            this.numOfBushes += 1;
            }
        }


    @Override public HashMap<Vector2d, Grass> getBushes() {
        return this.bushes;
    }

}
