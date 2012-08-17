/*
 * TuCSoN coordination infrastructure - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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
package alice.tucson.api;

import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InvalidTupleCentreIdException;

import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

import alice.tuprolog.Term;

import java.io.Serializable;

public class TucsonTupleCentreId implements alice.tuplecentre.api.TupleCentreId, Serializable{

	private static final long serialVersionUID = -4503481713163088789L;
	private Object tid;
	
	public TucsonTupleCentreId(Object id) throws TucsonInvalidTupleCentreIdException{		
		if(id.getClass().getName().equals("alice.respect.api.TupleCentreId"))
			tid = id;
		else{
			try{
				tid = new TupleCentreId((String) id);
			}catch(InvalidTupleCentreIdException e){
				throw new TucsonInvalidTupleCentreIdException();
			}
		}
	}

	public TucsonTupleCentreId(String tcName, String hostName, String portName)
			throws TucsonInvalidTupleCentreIdException{
		try{
			tid = new TupleCentreId(tcName, hostName, portName);
		}catch(InvalidTupleCentreIdException e){
			e.printStackTrace();
			throw new TucsonInvalidTupleCentreIdException();
		}
	}

	public Object getInternalTupleCentreId(){
		return tid;
	}
	
	public String toString(){
		return ((TupleCentreId) tid).toString();
	}
	
	public String getName(){
		return ((TupleCentreId) tid).getName();
	}

	public String getNode(){
		return ((TupleCentreId) tid).getNode();
	}
	
	public int getPort(){
		return ((TupleCentreId) tid).getPort();
	}

	public boolean isAgent(){
		return false;
	}

	public boolean isTC(){
		return true;
	}
	
	public boolean isEnv(){
		return false;
	}

	public Term toTerm() {
		return alice.tuprolog.Term.createTerm(tid.toString());
	}
	
}
