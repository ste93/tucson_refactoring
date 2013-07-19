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
public class TucsonMsgRequest implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

    /**
     * 
     * @param din
     *            the input stream where to read objects from
     * @return the request message received
     * @throws IOException
     *             if the stream has some problems
     * @throws ClassNotFoundException
     *             if the received object's class cannot be found
     */
    public static TucsonMsgRequest read(final ObjectInputStream din)
            throws IOException, ClassNotFoundException {
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
     *            the output stream where to send objects to
     * @param msg
     *            the request message to be sent
     * @throws IOException
     *             if the stream has some problems
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

    /**
     * 
     * @param i
     *            the operation id
     * @param ty
     *            the operation type code
     * @param stcid
     *            the String representation of the target tuple centre
     * @param t
     *            the tuple argument of the operation
     */
    public TucsonMsgRequest(final long i, final int ty, final String stcid,
            final LogicTuple t) {
        this.id = i;
        this.type = ty;
        this.tid = stcid;
        this.tuple = t;
    }

    /**
     * 
     */
    protected TucsonMsgRequest() {
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
     * @return the String representation of the target tuple centre
     */
    public String getTid() {
        return this.tid;
    }

    /**
     * 
     * @return the tuple argument of the operation
     */
    public LogicTuple getTuple() {
        return this.tuple;
    }

    /**
     * 
     * @return the operation type code
     */
    public int getType() {
        return this.type;
    }

}
