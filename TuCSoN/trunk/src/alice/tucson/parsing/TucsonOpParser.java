package alice.tucson.parsing;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidCommandException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.service.TucsonCmd;

/**
 * 
 * @author ste
 *
 */
public class TucsonOpParser {

	private String input;
	private String node;
	private String defPort; 
	private TucsonCmd tcmd;
	private TucsonTupleCentreId tid;
	private TucsonPrimitiveParser cmdParser;
	private TupleCentreIdParser tidParser;
	
	/**
	 * 
	 * @param input
	 * @param defPort
	 */
	public TucsonOpParser(String input, String node, int defPort){
		this.input = input;
		this.node = node;
		this.defPort = ""+defPort;
		tcmd = null;
		tid = null;
		cmdParser = null;
		tidParser = null;
	}
	
	/**
	 * 
	 * @throws TucsonInvalidCommandException
	 * @throws TucsonInvalidTupleCentreIdException
	 */
	public void parse() throws TucsonInvalidTupleCentreIdException{
		String cmd = input.trim();
		String tc = "default";
		int iOp = input.indexOf('?');
		int iBra = input.indexOf('(');
		if(iOp != -1){
			if(iBra != -1 && iBra < iOp){
				cmd = input.substring(iOp + 1, input.length()).trim();
				tc = input.substring(0, iOp).trim();
			}else if(iBra == -1 || iOp < iBra){
				cmd = input.substring(iOp + 1, input.length()).trim();
				tc = input.substring(0, iOp).trim();
			}
		}
		cmdParser = new TucsonPrimitiveParser(cmd);
		tidParser = new TupleCentreIdParser(tc, node, defPort);
		tcmd = cmdParser.parse();
		tid = tidParser.parse();
	}
	
	public TucsonCmd getCmd(){
		return tcmd;
	}
	
	public TucsonTupleCentreId getTid(){
		return tid;
	}
	
}
