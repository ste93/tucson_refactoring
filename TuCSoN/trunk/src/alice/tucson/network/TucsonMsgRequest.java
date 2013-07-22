package alice.tucson.network;

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

    @Override
    public String toString() {
        String s = "ID: " + this.id;
        s += "; Type: " + this.type;
        s += "; TID: " + this.tid;
        s += "; Tuple: " + this.tuple;
        return s;
    }

}
