package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.respect.api.IRespectTC;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;

/*
 * Created by Enrico Romagnoli
 * 
 * A new abstract class for the context
 */

public abstract class AbstractContext {
	
	private IRespectTC core;
	
	public AbstractContext(IRespectTC core){
		this.core = core;
	}
	
	protected IRespectTC getCore(){
		return core;
	}
	
	protected LogicTuple unify(TupleTemplate template, Tuple tuple){
		
//		System.out.println("[AbstractContext] unify: ci entro");
		
		boolean res = template.propagate(core.getVM().getRespectVMContext().getPrologCore(), tuple);
		if(res){
			return (LogicTuple) template;
		}else{
			return null;
		}
	}

}
