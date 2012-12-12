package alice.respect.core;

import alice.logictuple.BioTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.IBioSynchInterface;
import alice.respect.api.IRespectOperation;
import alice.respect.api.IRespectTC;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

public class BioSynchInterface extends RootInterface implements IBioSynchInterface{

	public BioSynchInterface(IRespectTC core){
        super(core);
    }
	
	@Override
	public void out(IId aid, BioTuple t) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		if (t==null)
            throw new InvalidLogicTupleException();
		IRespectOperation op = getCore().out(aid,t);
		op.waitForOperationCompletion();
		
	}

	@Override
	public BioTuple inv(IId aid, BioTuple t) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioTuple in(IId aid, BioTuple t) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioTuple rdv(IId aid, BioTuple t) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BioTuple rd(IId aid, BioTuple t) throws InvalidLogicTupleException,
			OperationNotPossibleException {
		// TODO Auto-generated method stub
		return null;
	}

}
