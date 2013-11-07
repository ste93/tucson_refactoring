/**
 * ActuatorTransducer.java
 */
package sm.nonjunit.situatedness;

import alice.respect.situatedness.AbstractTransducer;
import alice.respect.situatedness.ISimpleProbe;
import alice.respect.situatedness.TransducerId;
import alice.tucson.api.ITucsonOperation;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.core.AbstractTupleCentreOperation;

/**
 * @author ste (mailto: s.mariani@unibo.it) on 05/nov/2013
 * 
 */
public class ActuatorTransducer extends AbstractTransducer {

    /**
     * @param i
     * @param tc
     */
    public ActuatorTransducer(final TransducerId i, final TupleCentreId tc) {
        super(i, tc);
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.respect.situatedness.AbstractTransducer#getEnv(java.lang.String)
     */
    @Override
    public boolean getEnv(final String key) {
        System.err.println("[" + this.id
                + "]: I'm an actuator transducer, I can't sense values!");
        return false;
//        System.out.println("[" + this.id + "]: Reading...");
//        boolean success = true;
//        final Object[] keySet = this.probes.keySet().toArray();
//        for (final Object element : keySet) {
//            if (!((ISimpleProbe) this.probes.get(element)).readValue(key)) {
//                System.err.println("[" + this.id + "]: Read failure!");
//                success = false;
//                break;
//            }
//        }
//        return success;
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tuplecentre.core.OperationCompletionListener#operationCompleted
     * (alice.tuplecentre.core.AbstractTupleCentreOperation)
     */
    @Override
    public void operationCompleted(final AbstractTupleCentreOperation op) {
        /* not used */
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.tucson.api.TucsonOperationCompletionListener#operationCompleted
     * (alice.tucson.api.ITucsonOperation)
     */
    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /* not used */
    }

    /*
     * (non-Javadoc)
     * @see
     * alice.respect.situatedness.AbstractTransducer#setEnv(java.lang.String,
     * int)
     */
    @Override
    public boolean setEnv(final String key, final int value) {
        System.out.println("[" + this.id + "]: Writing...");
        boolean success = true;
        final Object[] keySet = this.probes.keySet().toArray();
        for (final Object element : keySet) {
            if (!((ISimpleProbe) this.probes.get(element)).writeValue(key,
                    value)) {
                System.err.println("[" + this.id + "]: Write failure!");
                success = false;
                break;
            }
        }
        return success;
    }

}
