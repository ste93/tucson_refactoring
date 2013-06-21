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

import java.util.Map;

import alice.tuplecentre.api.IId;
import alice.tuplecentre.api.TupleCentreId;

/**
 * Represents input events of the tuple centre virtual machine (only difference
 * w.r.t. Event is the <code>isLinking</code> field)
 * 
 * @author aricci
 */
public class InputEvent extends Event {

    private static final long serialVersionUID = -6321543805920861915L;
    private boolean isLinking;

    public InputEvent(final IId source, final TupleCentreOperation op,
            final TupleCentreId tc, final long time) {
        super(source, op, tc, time);
        this.isLinking = false;
    }

    public InputEvent(final IId source, final TupleCentreOperation op,
            final TupleCentreId tc, final long time,
            final Map<String, String> prop) {
        super(source, op, tc, time, prop);
        this.isLinking = false;
    }

    @Override
    public boolean isInput() {
        return true;
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    public boolean isLinking() {
        return this.isLinking;
    }

    @Override
    public boolean isOutput() {
        return false;
    }

    public void setIsLinking(final boolean flag) {
        this.isLinking = flag;
    }

    @Override
    public String toString() {
        return "[ src: " + this.getSource() + ", " + "op: "
                + this.getSimpleTCEvent() + ", " + "trg: " + this.getTarget()
                + ", " + "tc: " + this.getReactingTC() + " ]";
    }

}
