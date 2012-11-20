package alice.tucson.introspection;

import java.io.Serializable;

import alice.logictuple.LogicTuple;
import alice.tuplecentre.api.IId;

public class WSetEvent implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9193318251500885501L;
	private LogicTuple op;
	private IId source;
	private IId target;
	
	public WSetEvent(LogicTuple lt, IId s, IId t){
		op = lt;
		source = s;
		target = t;
	}

	/**
	 * @return the op
	 */
	public LogicTuple getOp() {
		return op;
	}

	/**
	 * @param op the op to set
	 */
	public void setOp(LogicTuple op) {
		this.op = op;
	}

	/**
	 * @return the source
	 */
	public IId getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(IId source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public IId getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(IId target) {
		this.target = target;
	}

}
