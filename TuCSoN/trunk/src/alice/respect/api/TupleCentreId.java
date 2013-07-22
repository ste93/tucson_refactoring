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
package alice.respect.api;

import alice.respect.api.exceptions.InvalidTupleCentreIdException;
import alice.respect.core.TupleCentreIdOperatorManager;
import alice.tuprolog.*;

/**
 * Tuple centre identifier for ReSpecT tuple centres
 * 
 * A tuple centre identifier must be a ground logic term.
 * 
 * @author aricci
 */
public class TupleCentreId implements alice.tuplecentre.api.TupleCentreId, java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private static TupleCentreIdOperatorManager opManager = new TupleCentreIdOperatorManager();
	protected alice.tuprolog.Term id;

	protected TupleCentreId(){}

	/**
	 * Constructs a tuple centre identifier from a string, which must represent
	 * a well-formed ground logic term
	 * 
	 * @param name
	 *            is the textual representation of the identifier
	 * @throws InvalidTupleCentreIdException
	 *             if name is not a well-formed ground logic term
	 */
	public TupleCentreId(String name) throws InvalidTupleCentreIdException{
		if(name.indexOf("@")<0)
			name +="@localhost";		
		try{
			id = Term.createTerm(name, opManager);
		}catch (Exception ex){
			throw new InvalidTupleCentreIdException();
		}
		if (!id.isGround())
			throw new InvalidTupleCentreIdException();
	}

	/**
	 * Constructs a tuple centre identifier, which must be a well-formed ground
	 * logic term
	 * 
	 * @param name
	 *            is the term representing the identifier
	 * @throws InvalidTupleCentreIdException
	 *             if name is not a well-formed ground logic term
	 */
	public TupleCentreId(Term name) throws InvalidTupleCentreIdException{
		id = name.getTerm();
		if (!id.isGround())
			throw new InvalidTupleCentreIdException();
	}

	public TupleCentreId(String tcName, String hostName, String portName) throws InvalidTupleCentreIdException{
		String tc = tcName.trim();
		String host = hostName.trim();
		String port = portName.trim();
		try{
			id = Term.createTerm(tc+"@"+host+":"+port, opManager);
		}catch (InvalidTermException e){
			throw new InvalidTupleCentreIdException();
		}
		
		if (!id.isGround())
			throw new InvalidTupleCentreIdException();
	}

	/**
	 * Provides the logic term representation of the identifier
	 * 
	 * @return the term representing the identifier
	 */
	public Term toTerm(){
		return id;
	}

	public String toString(){
		return id.toString();
	}

	public boolean isAgent(){
		return false;
	}

	public boolean isTC(){
		return true;
	}

	/**
	 * Gets the string representation of the tuple centre name
	 * 
	 * @return the ReSpecT node identifier
	 */
	public String getName(){
		if (id instanceof alice.tuprolog.Struct){
			Struct sid = (Struct) id;
			if (sid.getArity() == 2 && sid.getName().equals("@"))
				return sid.getArg(0).getTerm().toString();
		}
		return id.toString();
	}

	/**
	 * Gets localhost ReSpecT has no net infrastructure
	 * 
	 * @return the node identifier (localhost)
	 */
	public String getNode(){
		if (id instanceof alice.tuprolog.Struct){
			Struct sid = (Struct) id;
			if (sid.getArity() == 2 && sid.getName().equals("@")){
				Struct t = (Struct) sid.getArg(1);

				if(!t.getArg(0).isCompound()){
					return t.getArg(0).getTerm().toString();
				}else{
					Struct tt = (Struct) t.getArg(0);
					return tt.getArg(0).getTerm().toString() + "." + tt.getArg(1).getTerm().toString();
				}
			}
		}
		return "localhost";
	}
	
	public int getPort(){
		if (id instanceof alice.tuprolog.Struct){
			Struct sid = (Struct) id;
			if (sid.getArity() == 2 && sid.getName().equals("@")){
				Struct t = (Struct) sid.getArg(1);
				return Integer.parseInt(t.getArg(1).getTerm().toString());
			}
		}
		return 20504;
	}

	public boolean equals(Object o){
		return this.id.equals(((TupleCentreId) o).id);
	}

	public boolean isEnv() {
		return false;
	}
	
}
