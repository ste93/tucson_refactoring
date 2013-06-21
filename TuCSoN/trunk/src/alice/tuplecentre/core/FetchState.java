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

/**
 * This is the listening state of the TCVM
 * 
 * @author aricci
 */
public class FetchState extends TupleCentreVMState {

    private TupleCentreVMState reactingState;
    private TupleCentreVMState speakingState;

    public FetchState(final TupleCentreVMContext tcvm) {
        super(tcvm);
    }

    @Override
    public void execute() {
        this.vm.fetchPendingEvent();
        final InputEvent ev = this.vm.getCurrentEvent();
        System.out.println("[FetchState]: ev = " + ev);
        this.vm.addPendingQueryEvent(ev);
        this.vm.fetchTriggeredReactions(ev);
    }

    @Override
    public TupleCentreVMState getNextState() {
        if (this.vm.triggeredReaction()) {
            return this.reactingState;
        }
        return this.speakingState;
    }

    @Override
    public void resolveLinks() {
        this.reactingState = this.vm.getState("ReactingState");
        this.speakingState = this.vm.getState("SpeakingState");
    }

}
