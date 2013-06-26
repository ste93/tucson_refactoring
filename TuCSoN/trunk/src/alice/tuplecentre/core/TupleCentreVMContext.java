/*
 * Tuple Centre media - Copyright (C) 2001-2002 aliCE team at deis.unibo.it This
 * library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package alice.tuplecentre.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import alice.respect.core.RespectTC;
import alice.tuplecentre.api.AgentId;
import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.ITupleCentre;
import alice.tuplecentre.api.ITupleCentreManagement;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleCentreId;
import alice.tuplecentre.api.TupleTemplate;
import alice.tuplecentre.api.exceptions.OperationNotPossibleException;

/**
 * Defines the core abstract behaviour of a tuple centre virtual machine.
 * 
 * The class is abstract because the specific implementation of the reacting
 * behaviour and of the set management is left to the derived classes.
 * 
 * This class implements - by means of the state pattern - the behaviour
 * described formally in the article "From Tuple Space to Tuple Centre"
 * (Omicini, Denti) - Science of Computer Programming 2001,
 * 
 * 
 * @author aricci
 */
public abstract class TupleCentreVMContext implements ITupleCentreManagement,
        ITupleCentre {

    private long bootTime;
    private InputEvent currentEvent;
    private TupleCentreVMState currentState;
    private boolean doStep;
    private final List<Event> inputEnvEvents;
    private final List<Event> inputEvents;
    private boolean management;
    private final int maxPendingInputEventNumber;
    private final RespectTC respectTC;
    private final Map<String, TupleCentreVMState> states;
    private boolean stop;
    private final TupleCentreId tid;

    /**
     * Creates a new tuple centre virtual machine core
     * 
     * @param id
     *            is the tuple centre identifier
     * @param ieSize
     *            is the size of the input event queue
     */
    public TupleCentreVMContext(final TupleCentreId id, final int ieSize,
            final RespectTC rtc) {

        this.management = false;

        this.inputEvents = new LinkedList<Event>();
        this.inputEnvEvents = new LinkedList<Event>();

        this.tid = id;
        this.maxPendingInputEventNumber = ieSize;

        final TupleCentreVMState resetState = new ResetState(this);
        final TupleCentreVMState idleState = new IdleState(this);
        final TupleCentreVMState listeningState = new ListeningState(this);
        final TupleCentreVMState fetchState = new FetchState(this);
        final TupleCentreVMState fetchEnvState = new FetchEnvState(this);
        final TupleCentreVMState reactingState = new ReactingState(this);
        final TupleCentreVMState speakingState = new SpeakingState(this);

        this.states = new HashMap<String, TupleCentreVMState>();
        this.states.put("ResetState", resetState);
        this.states.put("IdleState", idleState);
        this.states.put("ListeningState", listeningState);
        this.states.put("FetchState", fetchState);
        this.states.put("FetchEnvState", fetchEnvState);
        this.states.put("ReactingState", reactingState);
        this.states.put("SpeakingState", speakingState);

        final Iterator<TupleCentreVMState> it = this.states.values().iterator();
        while (it.hasNext()) {
            it.next().resolveLinks();
        }
        this.currentState = resetState;

        this.respectTC = rtc;

    }

    public void addEnvInputEvent(final InputEvent in) {
        synchronized (this.inputEnvEvents) {
            this.inputEnvEvents.add(in);
        }
    }

    public void addInputEvent(final InputEvent in) {
        synchronized (this.inputEvents) {
            this.inputEvents.add(in);
        }
    }

    public abstract List<Tuple> addListTuple(Tuple t);

    /**
     * Adds a query to the pending query set (W) of the tuple centre
     * 
     * @param w
     *            the pending query to be added
     */
    public abstract void addPendingQueryEvent(InputEvent w);

    /**
     * Adds a tuple to the specification tuple set
     * 
     * @param t
     *            the tuple to be added
     */
    public abstract void addSpecTuple(Tuple t);

    /**
     * Adds a tuple to the tuple set (T)
     * 
     * @param t
     *            the tuple to be addedd
     */
    public abstract void addTuple(Tuple t);

    /**
     * Executes a new Operation.
     */
    public void doOperation(final IId who, final TupleCentreOperation op)
            throws OperationNotPossibleException {
        final InputEvent ev =
                new InputEvent(who, op, this.tid, this.getCurrentTime());
        synchronized (this.inputEvents) {
            if (this.inputEvents.size() > this.maxPendingInputEventNumber) {
                throw new OperationNotPossibleException();
            }
            this.inputEvents.add(ev);
        }
    }

    /**
     * Removes all tuples
     */
    public abstract void emptyTupleSet();

    /**
     * Evaluates a triggered reaction, changing the state of the VM accordingly.
     * 
     * @param z
     *            the triggered reaction to be evaluated
     */
    public abstract void evalReaction(TriggeredReaction z);

    /**
     * Executes a virtual machine behaviour cycle
     */
    public void execute() {

        try {
            if (this.management && this.stop) {
                if (!this.doStep) {
                    return;
                }
                this.doStep = false;
            }
            while (!this.currentState.isIdle()) {
                this.currentState.execute();
                this.currentState = this.currentState.getNextState();
                if (this.management && this.stop) {
                    if (!this.doStep) {
                        break;
                    }
                    this.doStep = false;
                }
            }
        } catch (final Exception ex) {
            this.notifyException(ex);
        }

    }

    public void fetchPendingEnvEvent() {
        if (this.pendingEnvEvents()) {
            try {
                synchronized (this.inputEnvEvents) {
                    this.currentEvent =
                            (InputEvent) (this.inputEnvEvents.remove(0));
                }
            } catch (final Exception ex) {
                this.notifyException(ex);
            }
        }
    }

    /**
     * Fetches a new pending input event.
     * 
     * The first pending input event is fetched from the queue as current event
     * subject of VM process.
     * 
     */
    public void fetchPendingEvent() {
        try {
            synchronized (this.inputEvents) {
                this.currentEvent = (InputEvent) (this.inputEvents.remove(0));
            }
        } catch (final Exception ex) {
            this.notifyException(ex);
        }
    }

    /**
     * Collects the time-triggered reactions
     * 
     * @param ev
     *            the event triggering reactions
     */
    public abstract void fetchTimedReactions(Event ev);

    /**
     * Collects the reactions that are triggered by an event
     * 
     * @param ev
     *            the event triggering reactions
     */
    public abstract void fetchTriggeredReactions(Event ev);

    /**
     * Gets all the tuples of the tuple centre
     * 
     * @return the whole tuple set
     */
    public abstract List<Tuple> getAllTuples();

    /**
     * Gets the boot time of the Tuple Centre VM
     * 
     * The time is expressed in millisecond, according to the standard Java
     * measurement of time.
     */
    public long getBootTime() {
        return this.bootTime;
    }

    /**
     * Gets the event currently processed by the virtual machine
     * 
     * @return
     */
    public InputEvent getCurrentEvent() {
        return this.currentEvent;
    }

    public String getCurrentState() {
        return this.currentState.getClass().getSimpleName();
    }

    /**
     * Gets current time of the Tuple Centre VM
     * 
     * The time is expressed in millisecond, according to the standard Java
     * measurement of time.
     */
    public long getCurrentTime() {
        return System.currentTimeMillis() - this.bootTime;
    }

    /**
     * Gets the identifier of this tuple centre
     * 
     */
    public TupleCentreId getId() {
        return this.tid;
    }

    /**
     * Gets an iterator over the pending query set (W)
     * 
     * @return the iterator
     */
    public abstract Iterator<? extends Event> getPendingQuerySetIterator();

    public RespectTC getRespectTC() {
        return this.respectTC;
    }

    public abstract Iterator<? extends Tuple> getSpecTupleSetIterator();

    /**
     * Gets a state of tuple centre virtual machine.
     * 
     * @param stateName
     *            name of the state
     * @return the state
     */
    public TupleCentreVMState getState(final String stateName) {
        return this.states.get(stateName);
    }

    /**
     * Gets an iterator over the set of triggered reactions
     * 
     * @return the iterator
     */
    public abstract Iterator<? extends TriggeredReaction>
            getTriggeredReactionSetIterator();

    /**
     * Gets an iterator over the tuple set (T)
     * 
     * @return the iterator
     */
    public abstract Iterator<? extends Tuple> getTupleSetIterator();

    public void goCommand() throws OperationNotPossibleException {
        if (!this.management) {
            throw new OperationNotPossibleException();
        }
        this.stop = false;
    }

    /**
     * Gets all the tuples of the tuple centre matching the TupleTemplate t
     * 
     * @return matching tuple
     */
    public abstract List<Tuple> inAllTuples(TupleTemplate t);

    public abstract void linkOperation(OutputEvent out);

    public void nextStepCommand() throws OperationNotPossibleException {
        if (!this.management) {
            throw new OperationNotPossibleException();
        }
        this.doStep = true;
    }

    public void notifyException(final Exception e) {
        e.printStackTrace();
    }

    public void notifyException(final String ex) {
        System.err.println(ex);
    }

    public boolean pendingEnvEvents() {
        try {
            synchronized (this.inputEnvEvents) {
                return this.inputEnvEvents.size() > 0;
            }
        } catch (final Exception ex) {
            this.notifyException(ex);
            return false;
        }
    }

    /**
     * Tests if there are pending input events
     * 
     * The method tests in there are input events to be processed (or rather if
     * the input event queue is not empty)
     */
    public boolean pendingEvents() {
        try {
            synchronized (this.inputEvents) {
                return this.inputEvents.size() > 0;
            }
        } catch (final Exception ex) {
            this.notifyException(ex);
            return false;
        }
    }

    /**
     * Gets all the tuples of the tuple centre matching the TupleTemplate t
     * without removing them
     * 
     * @return matching tuple
     */
    public abstract List<Tuple> readAllTuples(TupleTemplate t);

    public abstract Tuple readMatchingSpecTuple(TupleTemplate templateArgument);

    /**
     * Gets (not deterministically) without removing from the tuple set a tuple
     * that matches with the provided tuple template
     * 
     * @param t
     *            the tuple template that must be matched by the tuple
     * @return a tuple matching the tuple template
     */
    public abstract Tuple readMatchingTuple(TupleTemplate t);

    /**
     * Gets a tuple from tuple space in a non deterministic way
     * 
     * @return matching tuple
     */
    public abstract Tuple readUniformTuple(TupleTemplate t);

    public abstract Tuple
            removeMatchingSpecTuple(TupleTemplate templateArgument);

    /**
     * Removes (not deterministically) from the tuple set a tuple that matches
     * with the provided tuple template
     * 
     * @param t
     *            the tuple template that must be matched by the tuple
     * @return a tuple matching the tuple template
     */
    public abstract Tuple removeMatchingTuple(TupleTemplate t);

    /**
     * Removes the pending queries related to an agent
     * 
     * @param id
     *            is the agent identifies
     */
    public abstract void removePendingQueryEventsOf(AgentId id);

    /**
     * Removes a time-triggered reaction, previously fetched
     * 
     * @return the time-triggered reaction
     */
    public abstract TriggeredReaction removeTimeTriggeredReaction();

    /**
     * Removes a triggered reaction, previously fetched
     * 
     * @return the triggered reaction
     */
    public abstract TriggeredReaction removeTriggeredReaction();

    /**
     * Gets a tuple from tuple space in a non deterministic way
     * 
     * @return matching tuple
     */
    public abstract Tuple removeUniformTuple(TupleTemplate t);

    /**
     * Resets the tuple centre vm core.
     */
    public abstract void reset();

    public abstract void setAllSpecTuples(List<Tuple> tupleList);

    /**
     * Gets all the tuples of the tuple centre
     * 
     */
    public abstract void setAllTuples(List<Tuple> tupleList);

    public void setManagementMode(final boolean activate) {
        this.management = activate;
    }

    public abstract boolean spawnActivity(Tuple t, IId owner, IId targetTC);

    public void stopCommand() throws OperationNotPossibleException {
        if (!this.management) {
            throw new OperationNotPossibleException();
        }
        this.stop = true;
    }

    public abstract boolean time_triggeredReaction();

    public abstract boolean triggeredReaction();

    public abstract void updateSpecAfterTimedReaction(TriggeredReaction tr);

    /**
     * Specifies how to notify an output event.
     */
    protected void notifyOutputEvent(final OutputEvent ev) {
        ev.getSimpleTCEvent().notifyCompletion();
    }

    protected void setBootTime() {
        this.bootTime = System.currentTimeMillis();
    }

}
