package alice.respect.core;

import alice.respect.api.ILinkContext;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.core.TupleCentreOperation;

public class LinkContext implements ILinkContext {

    private final RespectVM vm;

    public LinkContext(final RespectVM rvm) {
        this.vm = rvm;
    }

    public void doOperation(final TupleCentreId id,
            final TupleCentreOperation op) throws OperationNotPossibleException {
        this.vm.doOperation(id, (RespectOperation) op);
    }

}
