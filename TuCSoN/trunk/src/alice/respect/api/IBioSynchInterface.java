package alice.respect.api;

import alice.logictuple.BioTuple;
import alice.logictuple.exceptions.InvalidLogicTupleException;
import alice.respect.api.exceptions.OperationNotPossibleException;
import alice.tuplecentre.api.IId;

public interface IBioSynchInterface {

	void out(IId aid, BioTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;

	BioTuple inv(IId aid, BioTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	BioTuple in(IId aid, BioTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
		
	BioTuple rdv(IId aid, BioTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
	BioTuple rd(IId aid, BioTuple t) throws InvalidLogicTupleException, OperationNotPossibleException;
	
}
