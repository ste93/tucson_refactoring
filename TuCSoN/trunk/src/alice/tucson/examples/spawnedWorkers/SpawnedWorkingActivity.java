package alice.tucson.examples.spawnedWorkers;

import java.math.BigInteger;

import alice.logictuple.LogicTuple;
import alice.logictuple.TupleArgument;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.logictuple.exceptions.InvalidTupleOperationException;
import alice.tucson.api.SpawnActivity;

/**
 * 
 * 
 * @author s.mariani@unibo.it
 */
public class SpawnedWorkingActivity extends SpawnActivity {

	private static final long serialVersionUID = -4459068799410719933L;

	/*
	 * 
	 */
	@Override
	public void doActivity() {
		try{
			/*
			 * Jobs collection phase.
			 */
			LogicTuple templ = LogicTuple.parse("fact(master(M),num(N),reqID(R))");
			log("Waiting for jobs...");
			/*
			 * Watch out: it's a suspensive primitive! If no jobs are available
			 * we are stuck!
			 */
			LogicTuple job = in(templ);
			log("Found job: "+job.toString());
			/*
			 * Computation phase.
			 */
			BigInteger bigNum = computeFactorial(job.getArg("num").getArg(0));
			/*
			 * Result submission phase.
			 */
			LogicTuple res = LogicTuple.parse("res(" +
					"master("+job.getArg("master").getArg(0)+")," +
					"fact("+bigNum.toString()+")," +
					"reqID("+job.getArg("reqID").getArg(0)+")" +
				")");
			log("Putting result: "+res.toString());
			out(res);
		}catch(InvalidLogicTupleException e){
			log("ERROR: Tuple is not an admissible Prolog term!");
			e.printStackTrace();
		} catch (InvalidTupleOperationException e) {
			log("ERROR: No tuple arguments to retrieve!");
			e.printStackTrace();
		}
	}

	private BigInteger computeFactorial(TupleArgument varValue) {
		try {
			int num = varValue.intValue();
			log("Computing factorial for: "+num+"...");
			return factorial(num);
		} catch (InvalidTupleOperationException e) {
			log("Not an Integer value, killing myself...");
			return new BigInteger("-1");
		}
	}

	private BigInteger factorial(int num) {
		if(num == 0)
			return BigInteger.ONE;
		else
			return new BigInteger(""+num).multiply(factorial(num-1));
	}

}
