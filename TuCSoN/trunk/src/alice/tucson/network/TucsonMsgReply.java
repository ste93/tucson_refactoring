package alice.tucson.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import alice.logictuple.LogicTuple;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 03/lug/2013
 * 
 */
public class TucsonMsgReply implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param din
     *            the input stream where to read objects from
     * @return the reply message received
     * @throws IOException
     *             if the stream has some problems
     * @throws ClassNotFoundException
     *             if the received object's class cannot be found
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
     *            the output stream where to send objects to
     * @param msg
     *            the reply message to be sent
     * @throws IOException
     *             if the stream has some problems
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

    /**
     * 
     * @param i
     *            the operation id
     * @param t
     *            the operation type code
     * @param a
     *            wether the operation is allowed
     * @param s
     *            wether the operation completed
     * @param ok
     *            wether the operation succeeded
     */
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

    /**
     * 
     * @param i
     *            the operation id
     * @param t
     *            the operation type code
     * @param a
     *            wether the operation is allowed
     * @param s
     *            wether the operation completed
     * @param ok
     *            wether the operation succeeded
     * @param req
     *            the tuple argument of the operation
     * @param res
     *            the object result of the operation (can be a tuple or a list
     *            of tuples)
     */
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

    /**
     * 
     */
    protected TucsonMsgReply() {
        /*
         * 
         */
    }

    /**
     * 
     * @return the operation id
     */
    public long getId() {
        return this.id;
    }

    /**
     * 
     * @return the tuple argument of the operation
     */
    public LogicTuple getTupleRequested() {
        return this.reqTuple;
    }

    /**
     * 
     * @return the object result of the operation (can be a tuple or a tuple
     *         list)
     */
    public Object getTupleResult() {
        return this.resTuple;
    }

    /**
     * 
     * @return the type code of the operation
     */
    public int getType() {
        return this.type;
    }

    /**
     * 
     * @return wether the operation is allowed
     */
    public boolean isAllowed() {
        return this.allowed;
    }

    /**
     * 
     * @return wether the operation succeeded
     */
    public boolean isResultSuccess() {
        return this.resultSuccess;
    }

    /**
     * 
     * @return wether the operation completed
     */
    public boolean isSuccess() {
        return this.success;
    }

}
