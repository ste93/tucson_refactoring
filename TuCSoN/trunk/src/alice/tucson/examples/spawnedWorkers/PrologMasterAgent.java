/**
 * PrologMasterAgent.java
 */
package alice.tucson.examples.spawnedWorkers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import alice.tuprolog.InvalidLibraryException;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 26/lug/2013
 * 
 */
public class PrologMasterAgent {

    /*
     * Relative path w.r.t. to running location (project root).
     */
    private static final String DEFAULT_THEORY_PATH =
            "alice/tucson/examples/spawnedWorkers/masterAgent.pl";
    /*
     * Remember the dot!
     */
    private static final String DEFAULT_GOAL =
            "runMasterAgent(master-agent, [default@localhost:20504], 10, 5).";
    /*
     * The necessary Tucson2PLibrary...
     */
    private static final String DEFAULT_LIBRARY_NAME =
            "alice.tucson.api.Tucson2PLibrary";
    /*
     * ...can be found within TuCSoN .jar
     */
    private static final String DEFAULT_LIBRARY_PATH = "./";
    private static final String ME = "PrologMasterAgent";

    /**
     * @param args
     */
    public static void main(String[] args) {
        /*
         * 1) Get the Prolog program (theory) to run.
         */
        String sTheory = null;
        try {
            if (args.length > 0) {
                sTheory = PrologMasterAgent.fileToString(args[0]);
            } else {
                sTheory =
                        PrologMasterAgent
                                .fileToString(PrologMasterAgent.DEFAULT_THEORY_PATH);
            }
        } catch (IOException e) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.SEVERE,
                    "IOException");
            e.printStackTrace();
            System.exit(-1);
        }
        /*
         * 2) Boot a tuProlog engine with required libraries (Tucson2PLibrary)
         * loaded.
         */
        Prolog engine = new Prolog();
        try {
            engine.loadLibrary(PrologMasterAgent.DEFAULT_LIBRARY_NAME,
                    new String[] { PrologMasterAgent.DEFAULT_LIBRARY_PATH });
        } catch (InvalidLibraryException e) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.SEVERE,
                    "InvalidLibraryException");
            System.exit(-1);
        }
        /*
         * 3) Set the theory into the engine.
         */
        try {
            engine.setTheory(new Theory(sTheory));
        } catch (InvalidTheoryException e) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.SEVERE,
                    "InvalidTheoryException");
            System.exit(-1);
        }
        /*
         * 4) [OPTIONAL] Capture tuProlog output and redirect it to Java Logger.
         */
        engine.addOutputListener(new OutputListener() {

            public void onOutput(OutputEvent arg0) {
                System.out.println(arg0.getMsg());
            }
        });
        /*
         * 5) Solve a given goal using the engine on the theory.
         */
        SolveInfo info = null;
        try {
            if (args.length > 1) {
                info = engine.solve(args[1]);
            } else {
                info = engine.solve(PrologMasterAgent.DEFAULT_GOAL);
            }
        } catch (MalformedGoalException e) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.SEVERE,
                    "MalformedGoalException");
            System.exit(-1);
        }
        try {
            while (info.hasOpenAlternatives()) {
                Logger.getLogger(PrologMasterAgent.ME).log(Level.INFO,
                        info.getSolution().toString());
                engine.solveNext();
            }
        } catch (NoSolutionException e) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.INFO,
                    "No solution found.");
        } catch (NoMoreSolutionException e) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.INFO,
                    "No more solutions to explore.");
        }
    }

    private static String fileToString(String path) throws IOException {
        InputStream in =
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(path);
        if (in == null) {
            Logger.getLogger(PrologMasterAgent.ME).log(Level.SEVERE,
                    "No input stream found.");
            System.exit(-1);
        }
        BufferedInputStream br = new BufferedInputStream(in);
        byte[] res = new byte[br.available()];
        br.read(res);
        br.close();
        return new String(res);
    }

}
