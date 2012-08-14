package alice.tucson.parsing;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exception.InvalidTupleOperationException;

import static alice.util.Tools.removeApices;

/**
 * 
 * @author ste
 *
 */
public class RespectReactionParser {
	
	private LogicTuple t;
	private String spec;
	private int nGuards;
	boolean flag;
	private int nTimes;
	
	public RespectReactionParser(LogicTuple t){
		this.t = t;
		spec = "'reaction(";
		nGuards = 0;
		flag = false;
		nTimes = 0;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public String parse() {
//		log("t = " + t);
		try {
//			log("t.getName() = " + t.getName());
			if(t.getName().equals("[]"))
				return "";
			for(int i = 0; i < t.getArity(); i++){
//				log("t.getArg("+i+") = " + t.getArg(i));
				parse(t.getArg(i));
			}
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
		log("spec = " + spec.substring(1, spec.length()-1));
		return spec.substring(1, spec.length()-1);
	}

	/**
	 * 
	 * @param arg
	 */
	private void parse(TupleArgument arg) {
		try {
			if(arg.toString().equals("[]")){
				if(nTimes == 1)
					spec = (spec.substring(0, spec.length()-1))+")).'";
				else
					spec = (spec.substring(0, spec.length()-1))+".'";
			}else if(removeApices(arg.getName()).equals(",")){
				for(int i = 0; i < arg.getArity(); i++){
//					log("[,] arg.getArg("+i+") = " + arg.getArg(i));
					parse(arg.getArg(i));
				}
				if(flag){
					nTimes++;
					if(nTimes == 1){
						spec = spec.substring(0, spec.length()-1);
						spec += ")).";
					}
				}
			}else if(removeApices(arg.getName()).equals(".")){
//				spec = spec.substring(0, spec.length()-1);
//				spec += ")). reaction(";
				spec += " reaction(";
				nGuards = 0;
				flag = false;
				nTimes = 0;
				for(int i = 0; i < arg.getArity(); i++){
//					log("[.] arg.getArg("+i+") = " + arg.getArg(i));
					parse(arg.getArg(i));
				}
			}else{
				if(bigFatCondition(arg)){
					nGuards++;
					if(nGuards == 1){
						flag = false;
						spec += "("+arg+",";
//						log("spec = " + spec);
					}else{
						spec += arg+",";
//						log("spec = " + spec);
					}
				}else{
					if(nGuards > 0){
						nGuards = 0;
						flag = true;
						spec = spec.substring(0, spec.length()-1);
						spec += "),("+arg+",";
					}else{
						spec += arg+",";
//						log("spec = " + spec);
					}
				}
			}
		} catch (InvalidTupleOperationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param arg
	 * @return
	 * @throws InvalidTupleOperationException
	 */
	private boolean bigFatCondition(TupleArgument arg) throws InvalidTupleOperationException{
		return arg.getName().equals("request") || arg.getName().equals("response") || 
				arg.getName().equals("success") || arg.getName().equals("failure") || 
				arg.getName().equals("endo") || arg.getName().equals("exo") || 
				arg.getName().equals("intra") || arg.getName().equals("inter") || 
				arg.getName().equals("from_agent") || arg.getName().equals("to_agent") || 
				arg.getName().equals("from_tc") || arg.getName().equals("to_tc") || 
				arg.getName().equals("before") || arg.getName().equals("after") || 
				arg.getName().equals("from_agent") || arg.getName().equals("invocation") || 
				arg.getName().equals("inv") || arg.getName().equals("req") || 
				arg.getName().equals("pre") || arg.getName().equals("completion") || 
				arg.getName().equals("compl") || arg.getName().equals("resp") || 
				arg.getName().equals("post") || arg.getName().equals("between") || 
				arg.getName().equals("operation") || arg.getName().equals("link_out") || 
				arg.getName().equals("link_in") || arg.getName().equals("internal");
	}
	
	/**
	 * 
	 * @param msg
	 */
	private void log(String msg){
		System.out.println("[RespectReactionParser]: " + msg);
	}

}
