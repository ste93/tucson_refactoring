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
 * @author aricci
 */
public class SpecificationSynchInterface extends RootInterface implements
        ISpecificationSynchInterface {

    public SpecificationSynchInterface(final IRespectTC core) {
        super(core);
    }

    public List<LogicTuple> get_s(final IId aid)
            throws OperationNotPossibleException {
        final IRespectOperation op = this.getCore().get_s(aid);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    public LogicTuple in_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().in_s(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple inp_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().inp_s(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple no_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().no_s(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple nop_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().nop_s(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public void out_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().out_s(id, t);
        op.waitForOperationCompletion();
    }

    public LogicTuple rd_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().rd_s(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public LogicTuple rdp_s(final AgentId id, final LogicTuple t)
            throws InvalidLogicTupleException, OperationNotPossibleException {
        if (t == null) {
            throw new InvalidLogicTupleException();
        }
        final IRespectOperation op = this.getCore().rdp_s(id, t);
        op.waitForOperationCompletion();
        return this.unify(t, op.getLogicTupleResult());
    }

    public List<LogicTuple> set_s(final IId aid, final LogicTuple t)
            throws OperationNotPossibleException {
        final IRespectOperation op = this.getCore().set_s(aid, t);
        op.waitForOperationCompletion();
        return op.getLogicTupleListResult();
    }

    public List<LogicTuple>
            set_s(final IId aid, final RespectSpecification spec)
                    throws OperationNotPossibleException,
                    InvalidSpecificationException {
        final IRespectOperation op = this.getCore().set_s(aid, spec);
        if (aid.toString().equals("'$TucsonNodeService-Agent'")
                || aid.toString().startsWith("'$Inspector-'")) {
            return new LinkedList<LogicTuple>();
        }
        return op.getLogicTupleListResult();
    }

}
