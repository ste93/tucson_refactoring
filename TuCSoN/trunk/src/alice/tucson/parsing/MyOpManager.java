package alice.tucson.parsing;

import alice.tuprolog.OperatorManager;

@SuppressWarnings("serial")
public class MyOpManager extends OperatorManager {
	
	public MyOpManager(){
		opNew(",","xfy",552);
		opNew("?","xfx",551);
		opNew("@","xfx",550);
        opNew(":","xfx",549);
        opNew(".","xfx",548);
	}

}
