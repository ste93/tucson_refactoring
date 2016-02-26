package it.unibo.mok.gui.impl.utils;

import java.util.ArrayList;
import java.util.Collections;

public class CircularPointGenerator {

    private int currentRing;
    private ArrayList<Integer> currentRingIndexes;
    private int objectsInCurrentRing;
    private int objectsInPreviousRings;
    private final int objectSize;
    private final int ringRadius;
    private double step;
    private int totalAdded;

    public CircularPointGenerator(final int ringRadius, final int objectSize) {
        this.ringRadius = ringRadius;
        this.objectSize = objectSize;
    }

    public void clear() {
        this.totalAdded = 0;
        this.currentRing = 0;
        this.objectsInCurrentRing = 0;
        this.objectsInPreviousRings = 0;
    }

    public Pair generateNext(final Pair genCenter) {
        Pair generatedCenter = null;
        if (this.totalAdded == 0) {
            generatedCenter = genCenter;
            this.objectsInPreviousRings = 0;
            this.objectsInCurrentRing = this.objectsInRing();
            this.step = this.step();
        } else {
            final int compIndex = this.totalAdded;
            if (compIndex - this.objectsInPreviousRings > this.objectsInCurrentRing) {
                this.currentRing++;
                this.objectsInPreviousRings = compIndex - 1;
                this.objectsInCurrentRing = this.objectsInRing();
                this.step = this.step();
                this.currentRingIndexes = new ArrayList<>();
                for (int i = 0; i < this.objectsInCurrentRing; i++) {
                    this.currentRingIndexes.add(i);
                }
                Collections.shuffle(this.currentRingIndexes);
            }
            final double angle = this.currentRingIndexes.get(compIndex
                    - this.objectsInPreviousRings - 1)
                    * this.step;
            final int x = (int) (genCenter.getFirst() + this.ringRadius
                    * this.currentRing * Math.cos(angle));
            final int y = (int) (genCenter.getSecond() + this.ringRadius
                    * this.currentRing * Math.sin(angle));
            generatedCenter = new Pair(x, y);
        }
        this.totalAdded++;
        return generatedCenter;
    }

    public int getCurrentRing() {
        return this.currentRing;
    }

    private int objectsInRing() {
        return (int) Math.floor(this.currentRing * this.ringRadius * 2
                * Math.PI / this.objectSize);
    }

    private double step() {
        return 2 * Math.PI / this.objectsInCurrentRing;
    }

}
