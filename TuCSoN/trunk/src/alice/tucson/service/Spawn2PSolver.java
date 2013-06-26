package alice.tucson.service;

import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Term;

public class Spawn2PSolver extends Thread {

    private final Term goal;
    private final Prolog solver;

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
            // TODO what to do?
        }
    }

}
