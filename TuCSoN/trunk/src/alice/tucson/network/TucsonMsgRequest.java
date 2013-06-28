package alice.tucson.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import alice.logictuple.LogicTuple;

public class TucsonMsgRequest extends TucsonMsg {

	private static final long serialVersionUID = -3954905186850436787L;

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

	public String toString() {
		String s = "ID: " + id;
		s += "; Type: " + type;
		s += "; TID: " + tid;
		s += "; Tuple: " + tuple;

		return s;
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
