package it.unibo.mok.gui.impl.animators;

import it.unibo.mok.gui.impl.utils.MathUtils;
import it.unibo.mok.gui.impl.utils.Pair;
import java.util.Random;

public class DanceAnimator {

    private final Pair DOWN;
    private int lastDirection = -1;
    private final Pair LEFT;
    private final Random random;
    private final Pair RIGHT;
    private final Pair UP;

    public DanceAnimator(final int gap) {
        this.random = new Random();
        this.UP = new Pair(0, -1 * gap);
        this.DOWN = new Pair(0, 1 * gap);
        this.LEFT = new Pair(-1 * gap, 0);
        this.RIGHT = new Pair(1 * gap, 0);
    }

    public Pair animate(Pair point) {
        if (this.lastDirection == -1) {
            switch (this.random.nextInt(4)) {
                case 0:
                    this.lastDirection = 0;
                    point = MathUtils.move(point, this.UP);
                    break;
                case 1:
                    this.lastDirection = 1;
                    point = MathUtils.move(point, this.DOWN);
                    break;
                case 2:
                    this.lastDirection = 2;
                    point = MathUtils.move(point, this.LEFT);
                    break;
                case 3:
                    this.lastDirection = 3;
                    point = MathUtils.move(point, this.RIGHT);
                    break;
            }
        } else {
            switch (this.lastDirection) {
                case 0:
                    point = MathUtils.move(point, this.DOWN);
                    break;
                case 1:
                    point = MathUtils.move(point, this.UP);
                    break;
                case 2:
                    point = MathUtils.move(point, this.RIGHT);
                    break;
                case 3:
                    point = MathUtils.move(point, this.LEFT);
                    break;
            }
            this.lastDirection = -1;

        }
        return point;
    }

}
