package alice.respect.api;

import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.core.TupleCentreOperation;

public interface ILinkContext {

    void doOperation(TupleCentreId id, TupleCentreOperation op)
            throws OperationNotPossibleException;

}
