package alice.tucson.network;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import alice.logictuple.LogicTuple;

public class TucsonMsgRequest extends TucsonMsg implements Externalizable {

	private long id;
	private int type;
	private LogicTuple tuple;
	private String tid;

	protected TucsonMsgRequest() {
	}

	public TucsonMsgRequest(long id, int type, String stcid, LogicTuple t) {
		this.id = id;
		this.type = type;
		this.tid = stcid;
		tuple = t;
	}

	public LogicTuple getTuple() {
		return tuple;
	}

	public long getId() {
		return id;
	}

	public String getTid() {
		return tid;
	}

	public int getType() {
		return type;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(id);
		out.writeInt(type);
		out.writeObject(tuple);
		out.writeUTF(tid);
		out.flush();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.id = in.readLong();
		this.type = in.readInt();
		this.tuple = (LogicTuple) in.readObject();
		this.tid = in.readUTF();
	}

	@Deprecated
	public static void write(ObjectOutputStream dout, TucsonMsgRequest msg) throws IOException {
		dout.writeLong(msg.getId());
		dout.writeInt(msg.getType());
		dout.writeUTF(msg.getTid());
		dout.writeObject(msg.getTuple());
	}

	@Deprecated
	public static TucsonMsgRequest read(ObjectInputStream din) throws Exception {
		long id = din.readLong();
		int type = din.readInt();
		String tid = din.readUTF();
		LogicTuple t = (LogicTuple) din.readObject();
		TucsonMsgRequest msg = new TucsonMsgRequest();
		msg.id = id;
		msg.type = type;
		msg.tuple = t;
		msg.tid = tid;
		return msg;
	}

}
