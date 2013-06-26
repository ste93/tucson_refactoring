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
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static TucsonMsgReply read(final ObjectInputStream din)
            throws IOException, ClassNotFoundException {
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
        msg.reqTuple = treq;
        msg.resTuple = tres;
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
    private LogicTuple reqTuple;
    private Object resTuple;
    private boolean resultSuccess;

    private boolean success;

    private int type;

    public TucsonMsgReply(final long i, final int t, final boolean a,
            final boolean s, final boolean ok) {
        this.id = i;
        this.type = t;
        this.allowed = a;
        this.success = s;
        this.reqTuple = null;
        this.resTuple = null;
        this.resultSuccess = ok;
    }

    public TucsonMsgReply(final long i, final int t, final boolean a,
            final boolean s, final boolean ok, final LogicTuple req,
            final Object res) {
        this.id = i;
        this.type = t;
        this.success = s;
        this.allowed = a;
        this.reqTuple = req;
        this.resTuple = res;
        this.resultSuccess = ok;
    }

    protected TucsonMsgReply() {
        /*
         * 
         */
    }

    public long getId() {
        return this.id;
    }

    public LogicTuple getTupleRequested() {
        return this.reqTuple;
    }

    public Object getTupleResult() {
        return this.resTuple;
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
