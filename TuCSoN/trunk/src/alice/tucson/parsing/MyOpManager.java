package alice.tucson.parsing;

import alice.tuprolog.Operator;
import alice.tuprolog.OperatorManager;
import alice.tuprolog.Prolog;

/**
 * 
 * @author ste (mailto: s.mariani@unibo.it) on 03/lug/2013
 * 
 */
public class MyOpManager extends OperatorManager {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public MyOpManager() {
        super();
        for (final Operator op : new Prolog().getOperatorManager()
                .getOperators()) {
            this.opNew(op.name, op.type, op.prio);
        }
        this.opNew("?", "xfx", 551);
        this.opNew("@", "xfx", 550);
        this.opNew(":", "xfx", 549);
        this.opNew(".", "xfx", 548);
    }

}
