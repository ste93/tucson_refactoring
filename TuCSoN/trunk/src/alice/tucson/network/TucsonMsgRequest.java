package alice.tucson.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import alice.logictuple.LogicTuple;

/**
 * 
 */
@SuppressWarnings("serial")
public class TucsonMsgRequest implements Serializable {

    /**
     * 
     * @param din
     * @return
     * @throws Exception
     */
    public static TucsonMsgRequest read(final ObjectInputStream din)
            throws Exception {
        final long id = din.readLong();
        final int type = din.readInt();
        final String tid = (String) din.readObject();
        final LogicTuple t = (LogicTuple) din.readObject();
        final TucsonMsgRequest msg = new TucsonMsgRequest();
        msg.id = id;
        msg.type = type;
        msg.tuple = t;
        msg.tid = tid;
        return msg;
    }

    /**
     * 
     * @param dout
     * @param msg
     * @throws IOException
     */
    public static void write(final ObjectOutputStream dout,
            final TucsonMsgRequest msg) throws IOException {
        dout.writeLong(msg.getId());
        dout.writeInt(msg.getType());
        dout.writeObject(msg.getTid());
        dout.writeObject(msg.getTuple());
    }

    private long id;
    private String tid;

    private LogicTuple tuple;

    private int type;

    public TucsonMsgRequest(final long i, final int ty, final String stcid,
            final LogicTuple t) {
        this.id = i;
        this.type = ty;
        this.tid = stcid;
        this.tuple = t;
    }

    protected TucsonMsgRequest() {

    }

    public long getId() {
        return this.id;
    }

    public String getTid() {
        return this.tid;
    }

    public LogicTuple getTuple() {
        return this.tuple;
    }

    public int getType() {
        return this.type;
    }

}
