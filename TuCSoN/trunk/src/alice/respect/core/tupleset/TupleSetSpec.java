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
package alice.respect.core.tupleset;

import java.util.LinkedList;
import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.respect.api.exceptions.InvalidAccessException;
import alice.respect.core.collection.DoubleKeyMVMap;

/**
 * Class representing a Tuple Set.
 *
 * @author Saverio Cicora
 *
 */
public class TupleSetSpec extends AbstractTupleSet {

    // TODO CICORA: ha senso avere due reaction perfettamente uguali nella
    // lista?
    // due normali tuple devono rimanere distinte anche se sono identiche, ma
    // per le reaction potrebbe essere un problema.
    /**
     *
     */
    public TupleSetSpec() {
        super();
        this.tuples = new DoubleKeyMVMap<String, String, LogicTuple>();
        this.tAdded = new LinkedList<LTEntry>();
        this.tRemoved = new LinkedList<LTEntry>();
        this.transaction = false;
    }

    @Override
    public String getTupleKey1(final LogicTuple t) {
        System.out.println("getTupleKey1) t = " + t);
        final TupleArgument event = t.getArg(0);
        System.out.println("getTupleKey1) event = " + event);
        if (event.isNumber()) {
            return event.toString();
        } else if (event.isVar()) {
            return "VAR";
        } else {
            return event.getPredicateIndicator();
        }
    }

    @Override
    public String getTupleKey2(final LogicTuple t)
            throws InvalidAccessException {
        System.out.println("getTupleKey2) t = " + t);
        final TupleArgument event = t.getArg(0);
        if (event.isAtomic()) {
            throw new InvalidAccessException(
                    "Attempt to access a non-existing second-level index item");
        }
        final TupleArgument eventArg = event.getArg(0);
        System.out.println("getTupleKey2) eventArg = " + eventArg);
        if (eventArg.isNumber()) {
            return eventArg.toString();
        } else if (eventArg.isVar()) {
            return "VAR";
        } else {
            return eventArg.getPredicateIndicator();
        }
    }
}
