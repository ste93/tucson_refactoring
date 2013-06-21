package alice.respect.core;

import alice.logictuple.LogicTuple;
import alice.respect.api.IRespectTC;
import alice.tuplecentre.api.Tuple;
import alice.tuplecentre.api.TupleTemplate;

/*
 * Created by Enrico Romagnoli A new abstract class for the context
 */
public abstract class RootInterface {

    private final IRespectTC core;

    public RootInterface(final IRespectTC rCore) {
        this.core = rCore;
    }

    protected IRespectTC getCore() {
        return this.core;
    }

    protected LogicTuple unify(final TupleTemplate template, final Tuple tuple) {
        final boolean res =
                template.propagate(this.core.getVM().getRespectVMContext()
                        .getPrologCore(), tuple);
        if (res) {
            return (LogicTuple) template;
        }
        return null;
    }

}
