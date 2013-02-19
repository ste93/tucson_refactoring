package alice.tucson.examples.helloWorld;

import alice.logictuple.LogicTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonAgent;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;
import alice.tucson.api.exceptions.TucsonOperationNotPossibleException;
import alice.tucson.api.exceptions.UnreachableNodeException;
import alice.tuplecentre.api.exceptions.OperationTimeOutException;

/**
 * Java TuCSoN Agent extending alice.tucson.api.TucsonAgent base class.
 * 
 * @author s.mariani@unibo.it
 */
/*
 * 1) Extend alice.tucson.api.TucsonAgent base class.
 */
public class HelloWorldAgent extends TucsonAgent {

	/*
	 * 2) Choose one of the given constructors.
	 */
	public HelloWorldAgent(String aid) throws TucsonInvalidAgentIdException {
		super(aid);
	}

	/*
	 * 3) To be overridden by TuCSoN programmers with their agent business logic.
	 */
	@Override
	protected void main() {
		/*
		 * 4) Get your ACC from the super-class.
		 */
		SynchACC acc = getContext();
		/*
		 * 5) Define the tuplecentre target of your coordination operations.
		 */
		try {
			TucsonTupleCentreId tid = new TucsonTupleCentreId("default", "localhost", "20504");
			/*
			 * 6) Build the tuple e.g. using TuCSoN parsing facilities.
			 */
			LogicTuple tuple = LogicTuple.parse("hello(world)");
			/*
			 * 7) Perform the coordination operation using the preferred coordination
			 * primitive.
			 */
			ITucsonOperation op = acc.out(tid, tuple, null);
			/*
			 * 8) Check requested operation success.
			 */
			LogicTuple res = null;
			if(op.isResultSuccess()){
				say("Operation succeeded.");
				/*
				 * 9) Get requested operation result.
				 */
				res = op.getLogicTupleResult();
				say("Operation result is "+res);
			}else{
				say("Operation failed.");
			}
			/*
			 * Another success test to be sure.
			 */
			LogicTuple template = LogicTuple.parse("hello(Who)");
			op = acc.rdp(tid, template, null);
			if(op.isResultSuccess()){
				res = op.getLogicTupleResult();
				say("Operation result is "+res);
			}else{
				say("Operation failed.");
			}
			/*
			 * ACC release is automatically done by the TucsonAgent base class.
			 */
		} catch (TucsonInvalidTupleCentreIdException e) {
			e.printStackTrace();
		} catch (InvalidLogicTupleException e) {
			/*
			 * String to be parsed is not in a valid Prolog syntax.
			 */
			e.printStackTrace();
		} catch (TucsonOperationNotPossibleException e) {
			e.printStackTrace();
		} catch (UnreachableNodeException e) {
			e.printStackTrace();
		} catch (OperationTimeOutException e) {
			e.printStackTrace();
		}
	}

	/*
	 * To override only for asynchronous coordination operations.
	 */
	@Override
	public void operationCompleted(ITucsonOperation arg0) { }

	/**
	 * @param args the name of the TuCSoN coordinable (optional).
	 */
	public static void main(String[] args) {
		String aid = null;
		if(args.length == 0)
			aid = "helloWorldAgent";
		else if(args.length == 1)
			aid = args[0];
		/*
		 * 10) Instantiate your agent and 11) start executing its 'main()' using
		 * method 'go()'.
		 */
		try {
			new HelloWorldAgent(aid).go();
		} catch (TucsonInvalidAgentIdException e) {
			e.printStackTrace();
		}
	}

}
