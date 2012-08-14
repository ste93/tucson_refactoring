package alice.respect.core;

import java.util.HashMap;
import alice.respect.api.IRespectTC;
import alice.respect.api.ITCRegistry;
import alice.respect.api.TupleCentreId;
import alice.respect.api.exceptions.InstantiationNotPossibleException;

public class RespectLocalRegistry implements ITCRegistry {

	/**
	 * internal representation of the registry, keys are
	 * tuple centre id (as String)
	 */
	private HashMap reg;
	
	/**
	 * default queue size for tuple centre implicitly created
	 * via getBlockingContext
	 */
	private int QUEUE_SIZE = 10;

	public RespectLocalRegistry(){
		this.reg = new HashMap();
	}

	/**
	 * Add a tuple centre to the registry. 
	 * WARNING: If a tuple centre with the same name yet exists 
	 * the specified tuple centre is not added to the registry.
	 * 
	 * @param tc the tuple centre to be added to the registry
	 */
	public void addTC(IRespectTC tc) {
		if (!reg.containsKey(tc.getId().getName()))
			reg.put(tc.getId().getName(), tc);
	}

	public IRespectTC getTC(TupleCentreId id)throws InstantiationNotPossibleException{
		if (!reg.containsKey(id.getName())) {
			throw new InstantiationNotPossibleException();
		}
		return (IRespectTC)reg.get(id.getName());
	}

	public int getSize(){
		return reg.size();
	}
	
	public HashMap getHashmap(){
		return reg;
	}

}
