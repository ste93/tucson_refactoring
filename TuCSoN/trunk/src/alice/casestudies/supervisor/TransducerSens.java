package alice.casestudies.supervisor;

import alice.respect.probe.ISimpleProbe;
import alice.respect.transducer.AbstractTransducer;
import alice.respect.transducer.TransducerId;
import alice.tucson.api.ITucsonOperation;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

public class TransducerSens extends AbstractTransducer {

    public TransducerSens(final TransducerId i, final TupleCentreId t) {
        super(i, t);
    }

    @Override
    public boolean getEnv(final String key) {
        try {
            boolean allReadsOk = false;
            final Object[] keySet = this.probes.keySet().toArray();
            for (final Object element : keySet) {
                allReadsOk =
                        ((ISimpleProbe) this.probes.get(element))
                                .readValue(key);
            }

            return allReadsOk;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void operationCompleted(final AbstractTupleCentreOperation op) {
        /*
         * 
         */
    }

    public void operationCompleted(final ITucsonOperation op) {
        /*
         * 
         */
    }

    @Override
    public boolean setEnv(final String key, final int value) {
        return false;
    }

}
