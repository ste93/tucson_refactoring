package alice.respect.core;

import java.util.List;

import alice.logictuple.LogicTuple;
import alice.respect.api.IManagementContext;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tucson.introspection.WSetEvent;
import alice.tuplecentre.api.InspectableEventListener;
import alice.tuplecentre.api.ObservableEventListener;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 01/lug/2013
 * 
 */
public class ManagementContext implements IManagementContext {

    private final RespectVM vm;

    /**
     * 
     * @param rvm
     *            the ReSpecT VM this context refers to
     */
    public ManagementContext(final RespectVM rvm) {
        this.vm = rvm;
    }

    public boolean abortOperation(final long opId) {
        return this.vm.abortOperation(opId);
    }

    public void addInspector(final InspectableEventListener l) {
        this.vm.addInspector(l);
    }

    public void addObserver(final ObservableEventListener l) {
        this.vm.addObserver(l);
    }

    public RespectSpecification getSpec() {
        return (RespectSpecification) this.vm.getReactionSpec();
    }

    public LogicTuple[] getTRSet(final LogicTuple t) {
        return this.vm.getTRSet();
    }

    public LogicTuple[] getTSet(final LogicTuple t) {
        return this.vm.getTSet(t);
    }

    public WSetEvent[] getWSet(final LogicTuple t) {
        return this.vm.getWSet(t);
    }

    public void goCommand() throws OperationNotPossibleException {
        this.vm.goCommand();
    }

    public boolean hasInspectors() {
        return this.vm.hasInspectors();
    }

    public boolean hasObservers() {
        return this.vm.hasObservers();
    }

    public void nextStepCommand() throws OperationNotPossibleException {
        this.vm.nextStepCommand();
    }

    public void removeInspector(final InspectableEventListener l) {
        this.vm.removeInspector(l);
    }

    public void removeObserver(final ObservableEventListener l) {
        this.vm.removeObserver(l);
    }

    public void reset() {
        this.vm.reset();
    }

    public void setManagementMode(final boolean activate) {
        this.vm.setManagementMode(activate);
    }

    public void setSpec(final RespectSpecification spec)
            throws InvalidSpecificationException {
        final boolean accepted = this.vm.setReactionSpec(spec);
        if (!accepted) {
            throw new InvalidSpecificationException();
        }
    }

    public void setWSet(final List<LogicTuple> wSet) {
        this.vm.setWSet(wSet);
    }

    public void stopCommand() throws OperationNotPossibleException {
        this.vm.stopCommand();
    }

}
