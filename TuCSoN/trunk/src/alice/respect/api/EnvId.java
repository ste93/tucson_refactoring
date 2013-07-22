package alice.respect.api;

import alice.tuplecentre.api.IId;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

public class EnvId implements IId{

	private String localName;
	private Struct id;
	
	public EnvId( String id ){
		this.localName = id;
		this.id= new Struct( id );
	}
	
	public String getLocalName(){
    	return localName;
	}
	
	public boolean isAgent() {
		return false;
	}
	
	public boolean isEnv() {
		return true;
	}
	
	public boolean isTC() {
		return false;
	}
	
	public Term toTerm(){
		if (id.getName().equals("@")){
			return id.getArg(0).getTerm();
		}
		return id.getTerm();
	}
	
}
