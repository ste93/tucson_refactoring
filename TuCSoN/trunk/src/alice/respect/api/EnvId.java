package alice.respect.api;

import alice.tuplecentre.api.IId;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 27/giu/2013
 * 
 */
public class EnvId implements IId {

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
