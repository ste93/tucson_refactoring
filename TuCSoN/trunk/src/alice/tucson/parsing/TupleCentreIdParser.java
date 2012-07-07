package alice.tucson.parsing;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

/**
 * 
 * @author ste
 *
 */
public class TupleCentreIdParser {
	
	private String input;
	private String node;
	private String defPort;
	
	/**
	 * 
	 * @param input
	 * @param defPort
	 */
	public TupleCentreIdParser(String input, String node, String defPort){
		this.input = input;
		this.node = node;
		this.defPort = defPort;
	}
	
	/**
	 * 
	 * @return
	 * @throws TucsonInvalidTupleCentreIdException
	 */
	public TucsonTupleCentreId parse() throws TucsonInvalidTupleCentreIdException{
		String tcName = input.trim();
		String hostName = node;
		String portName = defPort;
		int iAt = input.indexOf("@");
		int iCol = input.indexOf(":");
		if(iAt == 0){
			tcName = "default";
			if(iCol != -1){
				hostName = input.substring(iAt+1, iCol).trim();
//				System.out.println("@"+hostName+":");
				portName = input.substring(iCol+1, input.length()).trim();
//				System.out.println(":"+portName);
			}else{
				hostName = input.substring(iAt+1, input.length()).trim();
			}
		}else if(iAt != -1){
			tcName = input.substring(0, iAt).trim();
//			System.out.println(tcName+"@");
			if(iCol != -1){
				hostName = input.substring(iAt+1, iCol).trim();
//				System.out.println("@"+hostName+":");
				portName = input.substring(iCol+1, input.length()).trim();
//				System.out.println(":"+portName);
			}else{
				hostName = input.substring(iAt+1, input.length()).trim();
			}
		}else{
			if(iCol == 0){
				tcName = "default";
				portName = input.substring(iCol+1, input.length()).trim();
			}else if(iCol != -1){
				tcName = input.substring(0, iCol).trim();
				portName = input.substring(iCol+1, input.length()).trim();
			}
		}
//		System.out.println(tcName+"@"+hostName+":"+portName);
		return new TucsonTupleCentreId(tcName, hostName, portName);
	}
	
}
