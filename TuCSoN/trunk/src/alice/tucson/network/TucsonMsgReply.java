package alice.tucson.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import alice.logictuple.LogicTuple;

public class TucsonMsgReply extends TucsonMsg {

	private static final long serialVersionUID = -3427517966813327440L;

	private long id;
	private int type;
	private LogicTuple tuple_requested;
	private Object tuple_result;
	private boolean allowed;
	private boolean success;
	private boolean resultSuccess;

	protected TucsonMsgReply() {
	}

	public TucsonMsgReply(long id, int type, boolean allowed, boolean success, boolean ok) {
		this.id = id;
		this.type = type;
		this.allowed = allowed;
		this.success = success;
		tuple_requested = null;
		tuple_result = null;
		resultSuccess = ok;
	}

	public TucsonMsgReply(long id, int type, boolean allowed, boolean success, boolean ok, LogicTuple req, Object res) {
		this.id = id;
		this.type = type;
		this.success = success;
		this.allowed = allowed;
		tuple_requested = req;
		tuple_result = res;
		resultSuccess = ok;
	}

	public LogicTuple getTupleRequested() {
		return tuple_requested;
	}

	public Object getTupleResult() {
		return tuple_result;
	}

	public long getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isAllowed() {
		return allowed;
	}

	public boolean isResultSuccess() {
		return resultSuccess;
	}

	public String toString() {

		String s = "ID: " + id;
		s += "; Type: " + type;
		s += "; Tuple Requested: " + tuple_requested;
		s += "; Tuple Result: " + tuple_result;
		s += "; Allowed: " + allowed;
		s += "; Success: " + success;
		s += "; Result Success: " + resultSuccess;

		return s;
	}

	@Deprecated
	public static void write(ObjectOutputStream dout, TucsonMsgReply msg) throws IOException {
		dout.writeLong(msg.getId());
		dout.writeInt(msg.getType());
		dout.writeBoolean(msg.isAllowed());
		dout.writeBoolean(msg.isSuccess());
		dout.writeBoolean(msg.isResultSuccess());
		dout.writeObject(msg.getTupleRequested());
		dout.writeObject(msg.getTupleResult());
	}

	@Deprecated
	public static TucsonMsgReply read(ObjectInputStream din) throws Exception {
		long id = din.readLong();
		int type = din.readInt();
		boolean bool = din.readBoolean();
		boolean succ = din.readBoolean();
		boolean res = din.readBoolean();
		LogicTuple treq = (LogicTuple) din.readObject();
		Object tres = din.readObject();
		TucsonMsgReply msg = new TucsonMsgReply();
		msg.id = id;
		msg.type = type;
		msg.allowed = bool;
		msg.success = succ;
		msg.resultSuccess = res;
		msg.tuple_requested = treq;
		msg.tuple_result = tres;
		return msg;
	}

}
