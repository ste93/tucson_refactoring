package alice.respect.transducer;

import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.core.InternalEvent;
import alice.respect.probe.ProbeId;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Interface providing users only the admissible operations from the transducer.
 * 
 * @author Steven Maraldi
 * 
 */
public interface TransducerStandardInterface {

    TransducerId getIdentifier();

    ProbeId[] getProbes();

    TupleCentreId getTCId();

    void notifyEnvEvent(String key, int value)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    boolean notifyOutput(InternalEvent ev)
            throws InvalidTupleOperationException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException;
}
