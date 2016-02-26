package it.unibo.mok.gui.impl.utils;

public class Pair {

    private final int x;
    private final int y;

    public Pair(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int getFirst() {
        return this.x;
    }

    public int getSecond() {
        return this.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

}
