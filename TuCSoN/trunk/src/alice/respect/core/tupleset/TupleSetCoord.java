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
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.respect.core.collection.DoubleKeyMVMap;

public class TupleSetCoord extends AbstractTupleSet {

	public TupleSetCoord() {
		tuples = new DoubleKeyMVMap<String, String, LogicTuple>();
		tAdded = new LinkedList<LTEntry>();
		tRemoved = new LinkedList<LTEntry>();
		transaction = false;
	}

	/**
	 * Return the first level key. This key are based on functorn name and
	 * arity.
	 */
	@Override
	protected String getTupleKey1(LogicTuple t) throws alice.logictuple.exceptions.InvalidLogicTupleException {
		try {
			TupleArgument ta = t.getVarValue(null);
			if (ta != null)
				return ta.getPredicateIndicator();
			else
				return t.getPredicateIndicator();

		} catch (InvalidTupleOperationException e) {
			throw new alice.logictuple.exceptions.InvalidLogicTupleException();
		}
	}

	/**
	 * Returns the second level key. This key is based on the first term of the
	 * LogicTuple if exist, return an empty String otherwise. The variable are
	 * stored whit a special key.
	 * */
	@Override
	protected String getTupleKey2(LogicTuple t) throws InvalidLogicTupleException {

		try {
			TupleArgument tArg = t.getVarValue(null);
			// Check if the term as a value assigned to a variable
			if (tArg != null) {
				if (tArg.getArity() > 0) {
					tArg = tArg.getArg(0);
					if (tArg.isNumber())
						// The number are treated as a special case and
						// are indexed with their string representation
						return tArg.toString();
					// If a term are a variable, this is
					// indicized with a special key
					else if (tArg.isVar())
						return "VAR";
					else
						return tArg.getPredicateIndicator();
				} else
					return "";

			} else if (t.getArity() > 0) {
				tArg = t.getArg(0);
				if (tArg.isNumber())
					return tArg.toString();
				else if (tArg.isVar())
					return "VAR";
				else
					return tArg.getPredicateIndicator();

			} else
				return "";

		} catch (InvalidTupleOperationException e) {
			throw new alice.logictuple.exceptions.InvalidLogicTupleException();
		}
	}

}
