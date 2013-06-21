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
public class TucsonMsgReply implements Serializable {

    /**
     * 
     * @param din
     * @return
     * @throws Exception
     */
    public static TucsonMsgReply read(final ObjectInputStream din)
            throws Exception {
        final long id = din.readLong();
        final int type = din.readInt();
        final boolean bool = din.readBoolean();
        final boolean succ = din.readBoolean();
        final boolean res = din.readBoolean();
        final LogicTuple treq = (LogicTuple) din.readObject();
        final Object tres = din.readObject();
        final TucsonMsgReply msg = new TucsonMsgReply();
        msg.id = id;
        msg.type = type;
        msg.allowed = bool;
        msg.success = succ;
        msg.resultSuccess = res;
        msg.tuple_requested = treq;
        msg.tuple_result = tres;
        return msg;
    }

    /**
     * 
     * @param dout
     * @param msg
     * @throws IOException
     */
    public static void write(final ObjectOutputStream dout,
            final TucsonMsgReply msg) throws IOException {
        dout.writeLong(msg.getId());
        dout.writeInt(msg.getType());
        dout.writeBoolean(msg.isAllowed());
        dout.writeBoolean(msg.isSuccess());
        dout.writeBoolean(msg.isResultSuccess());
        dout.writeObject(msg.getTupleRequested());
        dout.writeObject(msg.getTupleResult());
    }

    private boolean allowed;
    private long id;
    private boolean resultSuccess;
    private boolean success;
    private LogicTuple tuple_requested;

    private Object tuple_result;

    private int type;

    public TucsonMsgReply(final long i, final int t, final boolean a,
            final boolean s, final boolean ok) {
        this.id = i;
        this.type = t;
        this.allowed = a;
        this.success = s;
        this.tuple_requested = null;
        this.tuple_result = null;
        this.resultSuccess = ok;
    }

    public TucsonMsgReply(final long i, final int t, final boolean a,
            final boolean s, final boolean ok, final LogicTuple req,
            final Object res) {
        this.id = i;
        this.type = t;
        this.success = s;
        this.allowed = a;
        this.tuple_requested = req;
        this.tuple_result = res;
        this.resultSuccess = ok;
    }

    protected TucsonMsgReply() {

    }

    public long getId() {
        return this.id;
    }

    public LogicTuple getTupleRequested() {
        return this.tuple_requested;
    }

    public Object getTupleResult() {
        return this.tuple_result;
    }

    public int getType() {
        return this.type;
    }

    public boolean isAllowed() {
        return this.allowed;
    }

    public boolean isResultSuccess() {
        return this.resultSuccess;
    }

    public boolean isSuccess() {
        return this.success;
    }

}
