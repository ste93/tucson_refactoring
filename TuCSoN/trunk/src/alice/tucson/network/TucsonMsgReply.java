package alice.tucson.network;

import java.io.Serializable;

import alice.logictuple.LogicTuple;

/**
 * 
 * @author Alessandro Ricci
 * @author (contributor) ste (mailto: s.mariani@unibo.it)
 * @author (contributor) Saverio Cicora
 * 
 */
public class TucsonMsgReply implements Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;

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

    @Override
    public String toString() {
        final StringBuffer s = new StringBuffer(87);
        s.append("ID: ");
        s.append(this.id);
        s.append("; Type: ");
        s.append(this.type);
        s.append("; Tuple Requested: ");
        s.append(this.reqTuple);
        s.append("; Tuple Result: ");
        s.append(this.resTuple);
        s.append("; Allowed: ");
        s.append(this.allowed);
        s.append("; Success: ");
        s.append(this.success);
        s.append("; Result Success: ");
        s.append(this.resultSuccess);
        return s.toString();
    }

}
