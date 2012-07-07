package alice.tucson.api.exceptions;

@SuppressWarnings("serial")
public class TucsonGenericException extends Exception{
	private String msg;
	public TucsonGenericException(String msg) {
		this.msg = msg;
	}	
	public String getMsg(){
		return msg;
	}
}
