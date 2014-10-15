/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package alice.respect.core;

import java.util.LinkedList;
import java.util.List;
import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.AgentId;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.ISpecificationSynchInterface;
import alice.respect.api.RespectSpecification;
import alice.respect.api.exceptions.InvalidSpecificationException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

/**
 * 
 * A Blocking Context wraps the access to a tuple centre virtual machine for a
 * specific thread of control, providing a blocking interface.
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public class SpecificationSynchInterface extends RootInterface implements
        ISpecificationSynchInterface {
    /**
     * 
     * @param core
     *            the ReSpecT tuple centres manager this interface refers to
     */
    public SpecificationSynchInterface(final IRespectTC core) {
        super(core);
    }

    @Override
    public List<LogicTuple> getS(final IId aid)
            throws OperationNotPossibleException {
        final IRespectOperation op = this.getCore().getS(aid);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    @Override
    public LogicTuple inpS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().inpS(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    @Override
    public LogicTuple inS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().inS(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    @Override
    public LogicTuple nopS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().nopS(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    @Override
    public LogicTuple noS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().noS(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    @Override
    public void outS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().outS(id, t);
        op.waitForOperationCompletion();
    }

    @Override
    public LogicTuple rdpS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().rdpS(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    @Override
    public LogicTuple rdS(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException("Null value");
        }
        final IRespectOperation op = this.getCore().rdS(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    @Override
    public List<LogicTuple> setS(final IId aid, final LogicTuple t)
            throws OperationNotPossibleException {
        final IRespectOperation op = this.getCore().setS(aid, t);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    @Override
    public List<LogicTuple> setS(final IId aid, final RespectSpecification spec)
            throws OperationNotPossibleException, InvalidSpecificationException {
        final IRespectOperation op = this.getCore().setS(aid, spec);
        if ("'$TucsonNodeService-Agent'".equals(aid.toString())
                || aid.toString().startsWith("'$Inspector-'")) {
            return new LinkedList<LogicTuple>();
        }
        return op.getLogicTupleListResult();
    }
}
