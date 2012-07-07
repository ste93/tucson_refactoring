package alice.tucson.api;

import alice.respect.api.AgentId;
import alice.respect.api.InvalidAgentIdException;

import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

import java.io.Serializable;
import java.util.UUID;

/**
 * 
 */
@SuppressWarnings("serial")
public class TucsonAgentId implements Serializable{
	
	private Object aid;
	private UUID uuid;
	
//	sure this is enough? better check which objects can be passed here other than String...
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
		}else
			return false;
		return true;
	}

	public boolean isAgent(){
		return true;
	}

	public boolean isTC(){
		return false;
	}

//	are you kidding?
	public boolean checkSyntax(){
		return true;
	}
	
	private String dropMinus(UUID uuid) {
		String uuids = uuid.toString();
//		System.out.println("uuids = " + uuids);
		String res = "";
		int j = 0;
		for(int i = 0; i < uuids.length(); i++){
			if(uuids.charAt(i) == '-'){
				res += uuids.substring(j, i);
				j = i+1;
			}
		}
		res += uuids.substring(j, uuids.length());
//		System.out.println("res = " + res);
		return res;
	}
		
}
