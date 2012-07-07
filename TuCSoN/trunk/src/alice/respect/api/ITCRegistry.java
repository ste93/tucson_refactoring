package alice.respect.api;

import java.util.HashMap;

public interface ITCRegistry {
	
	
	public IRespectTC getTC(TupleCentreId id) throws InstantiationNotPossibleException;
	
	public void addTC(IRespectTC tc);
	
	public HashMap getHashmap();

}
