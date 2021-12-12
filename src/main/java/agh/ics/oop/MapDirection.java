package agh.ics.oop;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    NORTHWEST,
    SOUTHEAST,
    SOUTHWEST,
    SOUTH,
    WEST,
    EAST;
    public String toString(){
        return switch (this) {
            case EAST -> "Wschód";
            case WEST -> "Zachód";
            case NORTH -> "Północ";
            case SOUTH -> "Południe";
            case SOUTHEAST -> "Południowy Wschód";
            case SOUTHWEST -> "Południowy Zachód";
            case NORTHEAST -> "Północny Wschód";
            case NORTHWEST -> "Północny Zachód";
        };
    }

//    public MapDirection next(){
//        return switch (this) {
//            case EAST -> SOUTH;
//            case WEST -> NORTH;
//            case NORTH -> EAST;
//            case SOUTH -> WEST;
//        };
//    }
//
//    public MapDirection previous(){
//        return switch (this) {
//            case EAST -> NORTH;
//            case WEST -> SOUTH;
//            case NORTH -> WEST;
//            case SOUTH -> EAST;
//        };
//    }

    public Vector2d toUnitVector(){
        return switch (this) {
            case EAST -> new Vector2d(1, 0);
            case WEST -> new Vector2d(-1, 0);
            case NORTH -> new Vector2d(0, 1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHEAST -> new Vector2d(1, -1);
            case SOUTHWEST -> new Vector2d(-1, -1);
            case NORTHEAST -> new Vector2d(1, 1);
            case NORTHWEST -> new Vector2d(-1, 1);
        };
    }

}

