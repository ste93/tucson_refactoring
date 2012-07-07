package alice.tucson.service;

public class TucsonCmd {

	private String primitive;
	private String arg;
	
	public TucsonCmd(String p, String a){
		primitive = p;
		arg = a;
	}
	
	public String getPrimitive(){
		return primitive;
	}
	
	public String getArg(){
		return arg;
	}
	
}
