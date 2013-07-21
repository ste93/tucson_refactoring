/*
 * ReSpecT - Copyright (C) aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.respect.core.tupleset;

import java.util.LinkedList;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.core.collection.MVHashMap;

public class TupleSetSimple extends AbstractTupleSet {

	public TupleSetSimple() {
		tuples = new MVHashMap<String, LogicTuple>();
		tAdded = new LinkedList<LogicTupleEntry>();
		tRemoved = new LinkedList<LogicTupleEntry>();
		transaction = false;
	}

	@Override
	protected String getKey(LogicTuple t) {
		try {
			return t.getPredicateIndicator();
		} catch (InvalidTupleOperationException e) {
			return "";
		}

	}
}
