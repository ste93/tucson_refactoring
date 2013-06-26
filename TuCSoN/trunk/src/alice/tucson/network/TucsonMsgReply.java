package alice.tucson.network;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import alice.logictuple.LogicTuple;

public class TucsonMsgReply extends TucsonMsg implements Externalizable {

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

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(id);
		out.writeInt(type);
		out.writeObject(tuple_requested);
		out.writeObject(tuple_result);
		out.writeBoolean(allowed);
		out.writeBoolean(success);
		out.writeBoolean(resultSuccess);
		out.flush();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.type = in.readInt();
		this.tuple_requested = (LogicTuple) in.readObject();
		this.tuple_result = in.readObject();
		this.allowed = in.readBoolean();
		this.success = in.readBoolean();
		this.resultSuccess = in.readBoolean();
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
