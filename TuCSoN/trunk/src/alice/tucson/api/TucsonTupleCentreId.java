package alice.tucson.api;

import alice.respect.api.InvalidTupleCentreIdException;
import alice.respect.api.TupleCentreId;

import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tuprolog.Term;

import java.io.Serializable;

/**
 * 
 */
@SuppressWarnings("serial")
public class TucsonTupleCentreId implements alice.tuplecentre.api.TupleCentreId, Serializable{

	private Object tid;
	
//	sure this is enough? better check which objects can be passed here other than String...
	public TucsonTupleCentreId(Object id) throws TucsonInvalidTupleCentreIdException{		
		if(id.getClass().getName().equals("alice.respect.api.TupleCentreId")){
			tid = id;
		}else{
			try{
				tid = new TupleCentreId((String) id);
			}catch(InvalidTupleCentreIdException e){
				throw new TucsonInvalidTupleCentreIdException();
			}
		}
	}

	public TucsonTupleCentreId(String tcName, String hostName, String portName) throws TucsonInvalidTupleCentreIdException{
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

//	are you kidding?
	public boolean checkSyntax(){
		return true;
	}

	public boolean isAgent(){
		return false;
	}

	public boolean isTC(){
		return true;
	}
	
//	when is this meant to return true?
	public boolean isEnv(){
		return false;
	}

//	TO CHECK: required by the ObservationService!
	public Term toTerm() {
		return alice.tuprolog.Term.createTerm(tid.toString());
	}
	
}
