package alice.tucson.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import alice.respect.api.ILinkContext;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 16/lug/2013
 * 
 */
public class InterTupleCentreACCProvider implements ILinkContext {

    class Executor extends Thread {

        private final TupleCentreId fromId;
        private InterTupleCentreACC helper;
        private final Map<String, InterTupleCentreACC> helpers;
        private final AbstractTupleCentreOperation op;
        private final alice.tuplecentre.api.TupleCentreId toId;

        public Executor(final alice.tuplecentre.api.TupleCentreId to,
                final TupleCentreId from, final AbstractTupleCentreOperation o,
                final Map<String, InterTupleCentreACC> helps) {
            super();
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
                    e.printStackTrace();
                }
                this.helpers.put(this.fromId.getNode(), this.helper);
            }

            try {
                this.helper.doOperation(this.toId, this.op);
            } catch (final TucsonOperationNotPossibleException e) {
                e.printStackTrace();
            } catch (final UnreachableNodeException e) {
                e.printStackTrace();
            }

        }

    }

    // FIXME How to fix this?
    private static ExecutorService exec;
    private static Map<String, InterTupleCentreACC> helpList;

    private final alice.tuplecentre.api.TupleCentreId idTo;

    /**
     * 
     * @param id
     *            the identifier of the tuple centre target of the linking
     *            invocation
     */
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
            final AbstractTupleCentreOperation op)
            throws OperationNotPossibleException {
        // id e' il tuplecentre source
        final Executor ex =
                new Executor(this.idTo, id, op,
                        InterTupleCentreACCProvider.helpList);
        InterTupleCentreACCProvider.exec.execute(ex);
    }

}
