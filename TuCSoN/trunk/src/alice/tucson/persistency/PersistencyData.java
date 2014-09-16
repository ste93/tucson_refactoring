package alice.tucson.persistency;

import java.util.List;

import alice.respect.core.TupleSet;
import alice.respect.core.tupleset.ITupleSet;

public class PersistencyData {
	
	private ITupleSet tupleSet; 
	private ITupleSet tupleSpecSet; 
	private TupleSet prologPredicates;
	
	private List<String> tuples;
	private List<String> specTuples;
	private List<String> predicates;
	private List<String> updates;
	
	private long initCreationPersistencyData;
	private long terminateCreationPersistencyData;
	
	
	public PersistencyData(ITupleSet tSet, ITupleSet tSpecSet, TupleSet pPrologPred, List<String> tUpdates)
	{
		this.initCreationPersistencyData=System.nanoTime();
		setTupleSet(tSet);
		setTupleSpecSet(tSpecSet);
		setPrologPredicates(pPrologPred);
		setUpdates(tUpdates);
		this.terminateCreationPersistencyData=System.nanoTime();
		
		this.log("Time elapsed from creation of PersistencyData: "+((this.terminateCreationPersistencyData-this.initCreationPersistencyData)/1000000)+" milliseconds.");
	}

	public PersistencyData(List<String>tuples,List<String>specTuples,List<String> pPredicates, List<String>updates)
	{
		this.initCreationPersistencyData=System.nanoTime();
		
		setTuples(tuples);
		setSpecTuples(specTuples);
		setPredicates(pPredicates);
		setUpdates(updates);

		this.terminateCreationPersistencyData=System.nanoTime();
		
		this.log("Time elapsed from creation of PersistencyData: "+((this.terminateCreationPersistencyData-this.initCreationPersistencyData)/10000000)+" milliseconds.");
	}
	
	public PersistencyData() {
		
	}

	public ITupleSet getTupleSet() {
		return tupleSet;
	}

	public void setTupleSet(ITupleSet tupleSet) {
		this.tupleSet = tupleSet;
	}

	public ITupleSet getTupleSpecSet() {
		return tupleSpecSet;
	}

	public void setTupleSpecSet(ITupleSet tupleSpecSet) {
		this.tupleSpecSet = tupleSpecSet;
	}

	public TupleSet getPrologPredicates() {
		return prologPredicates;
	}

	public void setPrologPredicates(TupleSet prologPredicates) {
		this.prologPredicates = prologPredicates;
	}

	public List<String> getUpdates() {
		return updates;
	}

	public void setUpdates(List<String> updates) {
		this.updates = updates;
	}

	public List<String> getTuples() {
		return tuples;
	}

	public void setTuples(List<String> tuples) {
		this.tuples = tuples;
	}

	public List<String> getSpecTuples() {
		return specTuples;
	}

	public void setSpecTuples(List<String> specTuples) {
		this.specTuples = specTuples;
	}

	public List<String> getPredicates() {
		return predicates;
	}

	public void setPredicates(List<String> pPredicates) {
		this.predicates = pPredicates;
	}
	
	private void log(String s){
		System.out.println("["+this.getClass().getName()+"] "+s);
	}
}
