package alice.tucson.service;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import alice.respect.api.ILinkContext;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.core.TupleCentreOperation;

/**
 * 
 */
public class InterTupleCentreACCProvider implements ILinkContext {

    class Executor extends Thread {

        private final TupleCentreId fromId;
        private InterTupleCentreACC helper;
        private final HashMap<String, InterTupleCentreACC> helpers;
        private final TupleCentreOperation op;
        private final alice.tuplecentre.api.TupleCentreId toId;

        public Executor(final alice.tuplecentre.api.TupleCentreId to,
                final TupleCentreId from, final TupleCentreOperation o,
                final HashMap<String, InterTupleCentreACC> helps) {
            this.toId = to;
            this.fromId = from;
            this.op = o;
            this.helpers = helps;
        }

        @Override
        public void run() {

            this.helper = this.helpers.get(this.fromId.getNode());
            if (this.helper == null) {
                try {
                    this.helper =
                            new InterTupleCentreACCProxy(
                                    new TucsonTupleCentreId(this.fromId));
                } catch (final TucsonInvalidTupleCentreIdException e) {
                    System.err
                            .println("[RespectInterTupleCentreContextProxy] Executor: "
                                    + e);
                    e.printStackTrace();
                }
                this.helpers.put(this.fromId.getNode(), this.helper);
            }

            try {
                this.helper.doOperation(this.toId, this.op);
            } catch (final TucsonOperationNotPossibleException e) {
                System.err
                        .println("[RespectInterTupleCentreContextProxy] Executor: "
                                + e);
                e.printStackTrace();
            } catch (final UnreachableNodeException e) {
                System.err
                        .println("[RespectInterTupleCentreContextProxy] Executor: "
                                + e);
                e.printStackTrace();
            }

        }

    }

    private static ExecutorService exec;
    // private final static int threadsNumber = Runtime.getRuntime()
    // .availableProcessors() + 1;
    private static HashMap<String, InterTupleCentreACC> helpList;

    private final alice.tuplecentre.api.TupleCentreId idTo;

    public InterTupleCentreACCProvider(
            final alice.tuplecentre.api.TupleCentreId id) {
        this.idTo = id;
        if (InterTupleCentreACCProvider.helpList == null) {
            InterTupleCentreACCProvider.helpList =
                    new HashMap<String, InterTupleCentreACC>();
        }
        if (InterTupleCentreACCProvider.exec == null) {
            InterTupleCentreACCProvider.exec = Executors.newCachedThreadPool();
        }
    }

    public synchronized void doOperation(final TupleCentreId id,
            final TupleCentreOperation op) throws OperationNotPossibleException {
        // id è il tuplecentre source
        final Executor ex =
                new Executor(this.idTo, id, op,
                        InterTupleCentreACCProvider.helpList);
        InterTupleCentreACCProvider.exec.execute(ex);
    }

}
