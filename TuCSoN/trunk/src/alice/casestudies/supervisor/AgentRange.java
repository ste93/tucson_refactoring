package alice.casestudies.supervisor;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

/**
 * 
 * Range agent ( LIGHT SUPERVISOR CASE STUDY )
 * 
 * @author Steven Maraldi
 * 
 */

public class AgentRange extends AbstractTucsonAgent {
    
    private static final String DEFAULT_PORT = "20504";

    private boolean iteraction = true;

    public AgentRange(final String aid) throws TucsonInvalidAgentIdException {
        super(aid);
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        /*
         * 
         */
    }

    public void stopAgent() {
        this.iteraction = false;
    }

    @Override
    protected void main() {
        final SynchACC acc = this.getContext();
        TucsonTupleCentreId tcIntensity = null;
        TucsonTupleCentreId tcRange = null;
        int intensity, min, max;
        LogicTuple t;

        try {
            tcIntensity =
                    new TucsonTupleCentreId("tc_intensity", "localhost",
                            DEFAULT_PORT);
            tcRange =
                    new TucsonTupleCentreId("tc_range", "localhost",
                            DEFAULT_PORT);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("range(Min,Max)");
                t = acc.rdp(tcRange, t, null).getLogicTupleResult();
                min = t.getArg(0).intValue();
                max = t.getArg(1).intValue();
                t = LogicTuple.parse("intensity(X)");
                intensity =
                        acc.inp(tcIntensity, t, null).getLogicTupleResult()
                                .getArg(0).intValue();

                if (intensity < min) {
                    t = LogicTuple.parse("data(intensity_to_set(" + min + "))");
                } else if (intensity > max) {
                    t = LogicTuple.parse("data(intensity_to_set(" + max + "))");
                } else {
                    t =
                            LogicTuple.parse("data(intensity_to_set("
                                    + intensity + "))");
                }

                acc.out(tcIntensity, t, null);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

    }

}
