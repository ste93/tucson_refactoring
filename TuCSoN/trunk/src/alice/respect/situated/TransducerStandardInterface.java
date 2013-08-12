package alice.respect.situated;

import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.core.InternalEvent;
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

    /**
     * 
     * @return the identifier of the transducer
     */
    TransducerId getIdentifier();

    /**
     * 
     * @return the list of Probes for which this tranduces is responsible for
     */
    AbstractProbeId[] getProbes();

    /**
     * 
     * @return the identifier of the TuCSoN tuple centre this transducer works
     *         with
     */
    TupleCentreId getTCId();

    /**
     * 
     * @param key
     *            the <code>key</code> of the environmental property change to
     *            be notified
     * @param value
     *            the <code>value</code> of the environmental property change to
     *            be notified
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be performed for some
     *             reason
     * @throws UnreachableNodeException
     *             if the TuCSoN tuple centre target of the notification cannot
     *             be reached over the network
     * @throws OperationTimeOutException
     *             if the notification operation expires timeout
     */
    void notifyEnvEvent(String key, int value)
            throws TucsonOperationNotPossibleException,
            UnreachableNodeException, OperationTimeOutException;

    /**
     * 
     * @param ev
     *            the ReSpecT event to be notified
     * @return wether the event has been succesfully notified
     * @throws InvalidTupleOperationException
     *             if the tuple representing the event cannot be managed
     *             properly
     * @throws TucsonOperationNotPossibleException
     *             if the requested operation cannot be performed for some
     *             reason
     * @throws UnreachableNodeException
     *             if the TuCSoN tuple centre target of the notification cannot
     *             be reached over the network
     * @throws OperationTimeOutException
     *             if the notification operation expires timeout
     */
    boolean notifyOutput(InternalEvent ev)
            throws InvalidTupleOperationException,
            TucsonOperationNotPossibleException, UnreachableNodeException,
            OperationTimeOutException;
}
