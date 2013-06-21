package alice.respect.api;

import alice.tuplecentre.api.IId;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

public class EnvId implements IId {

    private final Struct id;

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

    public Term toTerm() {
        if (this.id.getName().equals("@")) {
            return this.id.getArg(0).getTerm();
        }
        return this.id.getTerm();
    }

}
