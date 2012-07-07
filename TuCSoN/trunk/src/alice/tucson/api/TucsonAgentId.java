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

import alice.respect.api.AgentId;
import alice.respect.api.InvalidAgentIdException;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

import java.io.Serializable;
import java.util.UUID;

public class TucsonAgentId implements Serializable{
	
	private static final long serialVersionUID = -1027439718598689379L;
	private Object aid;
	private UUID uuid;
	
	public TucsonAgentId(Object id) throws TucsonInvalidAgentIdException{
		if(id.getClass().getName().equals("alice.respect.api.AgentId")){
			aid = id;
			uuid = null;
		}else{
			try{
				aid = new AgentId((String) id);
				uuid = null;
			}catch(InvalidAgentIdException e){
				throw new TucsonInvalidAgentIdException();
			}
		}
	}
	
	public String getAgentName(){
		return ((AgentId) aid).toString();
	}
	
	public String getAgentUUID(){
		return uuid.toString();
	}

	public Object getLocalAgentId(){
		return aid;
	}

	public String toString(){
		return ((AgentId) aid).toString();
	}
	
	public boolean assignUUID() throws TucsonInvalidAgentIdException{
		if(this.uuid == null){
			this.uuid = UUID.randomUUID();
			try {
				aid = new AgentId(aid+":uuid"+dropMinus(uuid));
			} catch (InvalidAgentIdException e) {
				throw new TucsonInvalidAgentIdException();
			}
		}
		return true;
	}

	public boolean isAgent(){
		return true;
	}

	public boolean isTC(){
		return false;
	}
	
	private String dropMinus(UUID uuid) {
		String uuids = uuid.toString();
		String res = "";
		int j = 0;
		for(int i = 0; i < uuids.length(); i++){
			if(uuids.charAt(i) == '-'){
				res += uuids.substring(j, i);
				j = i+1;
			}
		}
		res += uuids.substring(j, uuids.length());
		return res;
	}
		
}
