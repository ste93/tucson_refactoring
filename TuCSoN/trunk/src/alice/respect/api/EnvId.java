package alice.respect.api;

import alice.tuplecentre.api.IId;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * 
 * @author Unknown...
 * 
 */
public class EnvId implements IId, java.io.Serializable {

    /** serialVersionUID **/
    private static final long serialVersionUID = 1L;
    private final Struct id;
    private final String localName;

    /**
     * 
     * @param i
     *            the struct representing this environment identifier
     */
    public EnvId(final String i) {
        this.localName = i;
        this.id = new Struct(i);
    }

    /**
     * 
     * @return the String representation of the local name of an environmental
     *         resource
     */
    public String getLocalName() {
        return this.localName;
    }

    public boolean isAgent() {
        return false;
    }

    public boolean isEnv() {
        return true;
    }

    public boolean isTC() {
        return false;
    }

    /**
     * 
     * @return the term representation of this identifier
     */
    public Term toTerm() {
        if ("@".equals(this.id.getName())) {
            return this.id.getArg(0).getTerm();
        }
        return this.id.getTerm();
    }

}
