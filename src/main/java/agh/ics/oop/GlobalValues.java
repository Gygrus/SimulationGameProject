package agh.ics.oop;

import java.util.Comparator;

public interface GlobalValues {
    Vector2d[] dirToVec = {new Vector2d(0, 1), new Vector2d(1, 1), new Vector2d(1, 0), new Vector2d(1, -1), new Vector2d(0, -1), new Vector2d(-1, -1), new Vector2d(-1, 0), new Vector2d(-1, 1)};

    Comparator<Animal> comparatorAnimal = new Comparator<Animal>() {
        @Override
        public int compare(Animal o1, Animal o2) {
            if (o1.equals(o2)){
                return 0;
            }
            if (o2.getEnergy() == o1.getEnergy()){
                return 1;
            }
            return o2.getEnergy() - o1.getEnergy();
        }
    };
}
