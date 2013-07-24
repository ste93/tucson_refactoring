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

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.core.collection.BucketHashMap;

/**
 * Class representing a Tuple Set.
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 22/lug/2013
 * 
 */
public class TupleSetSpec extends AbstractTupleSet {

    // TODO CICORA: ha senso avere due reaction perfettamente uguali nella
    // lista?
    // due normali tuple devono rimanere distinte anche se sono identiche, ma
    // per le reaction potrebbe essere un problema.

    private static void log(final Object o) {
        System.out.println(o.toString());
    }

    /**
     * 
     */
    public TupleSetSpec() {
        super();
        this.tuples = new BucketHashMap<String, LogicTuple>();
        this.tAdded = new LinkedList<LogicTupleEntry>();
        this.tRemoved = new LinkedList<LogicTupleEntry>();
        this.transaction = false;
    }

    @Override
    protected String getKey(final LogicTuple t) {
        TupleSetSpec.log("");
        TupleSetSpec.log("La tupla e' " + t.toString());
        try {
            if (!"reaction".equals(t.getName())) {
                System.err
                        .println("Messaggio Saverio: TupleSpech ha un problema");
                // TODO CICORA: valutare se e' il caso di effettuare questo
                // controllo
                // throw new Exception();
            }
            final TupleArgument event = t.getArg(0);
            String key = event.getName();
            key = key.concat("-" + event.getArg(0).getPredicateIndicator());

            TupleSetSpec.log("La chiave e':" + key);

            return key;

        } catch (final InvalidTupleOperationException e) {
            e.printStackTrace();
        }
        /*
         * reaction( out(boot),
         * true,','(out(agent_list([])),','(out(tc_list([])),in(boot))) ).
         */

        return "";
    }

}
