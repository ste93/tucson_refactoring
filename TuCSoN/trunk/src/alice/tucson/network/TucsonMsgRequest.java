package alice.tucson.network;

import alice.logictuple.*;

import java.io.*;

/**
 * 
 */
@SuppressWarnings("serial")
public class TucsonMsgRequest implements Serializable{
	
	private long id;
	private int type;
	private LogicTuple tuple;
	private String tid;

	protected TucsonMsgRequest(){
		
	}

	public TucsonMsgRequest(long id, int type, String stcid, LogicTuple t){
		this.id = id;
		this.type = type;
		this.tid = stcid;
		tuple = t;
	}

	public LogicTuple getTuple(){
		return tuple;
	}

	public long getId(){
		return id;
	}

	public String getTid(){
		return tid;
	}

	public int getType(){
		return type;
	}

	/**
	 * 
	 * @param dout
	 * @param msg
	 * @throws IOException
	 */
	public static void write(ObjectOutputStream dout, TucsonMsgRequest msg) throws IOException{
		dout.writeLong(msg.getId());
		dout.writeInt(msg.getType());
		dout.writeObject(msg.getTid());
		dout.writeObject(msg.getTuple());
	}

	/**
	 * 
	 * @param din
	 * @return
	 * @throws Exception
	 */
	public static TucsonMsgRequest read(ObjectInputStream din) throws Exception{
		long id = din.readLong();
		int type = din.readInt();
		String tid = (String) din.readObject();
		LogicTuple t = (LogicTuple) din.readObject();
		TucsonMsgRequest msg = new TucsonMsgRequest();
		msg.id = id;
		msg.type = type;
		msg.tuple = t;
		msg.tid = tid;
		return msg;
	}
	
}
