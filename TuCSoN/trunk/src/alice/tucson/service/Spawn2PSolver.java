package alice.tucson.service;

import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Term;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 17/lug/2013
 * 
 */
public class Spawn2PSolver extends Thread {

    private final Term goal;
    private final Prolog solver;

    /**
     * 
     * @param s
     *            the Prolog engine to be used
     * @param g
     *            the goal to solve
     */
    public Spawn2PSolver(final Prolog s, final Term g) {
        super();
        this.solver = s;
        this.goal = g;
    }

    @Override
    public void run() {
        this.solver.solve(this.goal);
        try {
            do {
                this.solver.solveNext();
            } while (this.solver.hasOpenAlternatives());
            this.solver.solveEnd();
        } catch (final NoMoreSolutionException e) {
            /*
             * 
             */
        }
    }

}
