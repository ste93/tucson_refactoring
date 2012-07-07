package alice.tucson.parsing;

import alice.tucson.api.exceptions.TucsonInvalidCommandException;
import alice.tucson.service.TucsonCmd;

/**
 * 
 * @author ste
 *
 */
public class TucsonPrimitiveParser {
	
	private String input;
	
	/**
	 * 
	 * @param input
	 */
	public TucsonPrimitiveParser(String input){
		this.input = input;
	}
	
	/**
	 * 
	 * @return
	 * @throws TucsonInvalidCommandException
	 */
	public TucsonCmd parse(){
		String primitive = input.trim();
		String arg = "";
		int iLeftBra = input.indexOf("(");
		int iRightBra = input.lastIndexOf(")");
		if(iLeftBra != -1){
			primitive = input.substring(0, iLeftBra).trim();
			if(iRightBra != -1){
				arg = input.substring(iLeftBra+1, iRightBra).trim();
				if((primitive.equals("get") || primitive.equals("get_s")) && !arg.equals("")){
					primitive = "";
					arg = "";
					return null;
				}
			}else{
				primitive = "";
				return null;
//				throw new TucsonInvalidCommandException();
			}
		}else{
			if(!primitive.equals("quit") && !primitive.equals("exit") && !primitive.equals("get") 
					&& !primitive.equals("get_s") && !primitive.equals("help") && !primitive.equals("man") 
					&& !primitive.equals("syntax") && !primitive.equals("o/") && !primitive.equals("\\o") 
					&& !primitive.equals("hi")){
				primitive = "";
				return null;
//				throw new TucsonInvalidCommandException();
			}
		}
		return new TucsonCmd(primitive, arg);
	}
	
}
