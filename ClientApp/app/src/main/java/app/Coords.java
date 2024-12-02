package app;


import lombok.Data;


@Data public class Coords {
    public int x;
    public int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getRelX(boolean isWhite) {
        return isWhite ? x : 7 - x;
    }

    public int getRelY(boolean isWhite) {
        return isWhite ? y : 7 - y;
    }
}
