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
 * Abstract base class for Tuple Centre VM states.
 *
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 */
public abstract class AbstractTupleCentreVMState {

    /**
     *
     */
    protected AbstractTupleCentreVMContext vm;

    /**
     *
     * @param tcvm
     *            the tuple centre VM this state belongs to
     */
    public AbstractTupleCentreVMState(final AbstractTupleCentreVMContext tcvm) {
        this.vm = tcvm;
    }

    /**
     *
     */
    public abstract void execute();

    /**
     *
     * @return the next state in tuple centre VM execution flow
     */
    public abstract AbstractTupleCentreVMState getNextState();

    /**
     *
     * @return wether the tuple centre VM is idle
     */
    public abstract boolean isIdle();

    /**
     *
     */
    public abstract void resolveLinks();

    protected void log() {
        System.out.println("......=> " + this.getClass().getSimpleName());
    }
}
