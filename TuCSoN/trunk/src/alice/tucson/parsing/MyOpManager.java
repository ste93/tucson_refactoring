package alice.tucson.parsing;

import alice.tuprolog.Operator;
import alice.tuprolog.OperatorManager;
import alice.tuprolog.Prolog;

@SuppressWarnings("serial")
public class MyOpManager extends OperatorManager {
	
	public MyOpManager(){
        for(Operator op : new Prolog().getOperatorManager().getOperators())
        	opNew(op.name, op.type, op.prio);
		opNew("?","xfx",551);
		opNew("@","xfx",550);
        opNew(":","xfx",549);
        opNew(".","xfx",548);
	}

}
