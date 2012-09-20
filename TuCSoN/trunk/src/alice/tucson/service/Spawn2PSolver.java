package alice.tucson.service;

import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.Term;

public class Spawn2PSolver extends Thread {
	
	private Prolog solver;
	private Term goal;

	public Spawn2PSolver(Prolog s, Term g){
		solver = s;
		goal = g;
	}
	
	public void run(){
		solver.solve(goal);
		try{
	    	do{
				solver.solveNext();
//				Thread.sleep(100);
	    	}while(solver.hasOpenAlternatives());
	    	solver.solveEnd();
    	} catch (NoMoreSolutionException e) {
//			what to do?
		}
	}
	
}
