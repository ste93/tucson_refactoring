package alice.respect.api;

import alice.tuplecentre.api.IId;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * 
 * @author Unknown...
 * 
 */
public class EnvId implements IId {

    private final Struct id;

    /**
     * 
     * @param i
     *            the struct representing this environment identifier
     */
    public EnvId(final Struct i) {
        this.id = i;
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
