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

public class AG_Range extends AbstractTucsonAgent {

    private boolean iteraction = true;

    public AG_Range(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tc_intensity = null;
        TucsonTupleCentreId tc_range = null;
        int intensity, min, max;
        LogicTuple t;

        try {
            tc_intensity =
                    new TucsonTupleCentreId("tc_intensity", "localhost",
                            String.valueOf(20504));
            tc_range =
                    new TucsonTupleCentreId("tc_range", "localhost",
                            String.valueOf(20504));
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("range(Min,Max)");
                t = acc.rdp(tc_range, t, null).getLogicTupleResult();
                min = t.getArg(0).intValue();
                max = t.getArg(1).intValue();
                t = LogicTuple.parse("intensity(X)");
                intensity =
                        acc.inp(tc_intensity, t, null).getLogicTupleResult()
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

                acc.out(tc_intensity, t, null);
            } catch (final Exception e) {
                /*
                 * 
                 */
            }
        }

    }

}
