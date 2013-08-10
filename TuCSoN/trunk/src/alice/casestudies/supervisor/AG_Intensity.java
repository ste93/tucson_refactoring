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
 * Intensity check agent ( LIGHT SUPERVISOR CASE STUDY )
 * 
 * @author Steven Maraldi
 * 
 */

public class AG_Intensity extends AbstractTucsonAgent {

    private boolean iteraction = true;

    public AG_Intensity(final String aid) throws TucsonInvalidAgentIdException {
        super(aid);
    }

    @Override
    public void operationCompleted(final ITucsonOperation op) {
        // TODO Auto-generated method stub

    }

    public void stopAgent() {
        this.iteraction = false;
    }

    @Override
    protected void main() {
        // TODO Auto-generated method stub
        final SynchACC acc = this.getContext();
        TucsonTupleCentreId tc_intensity = null;
        TucsonTupleCentreId tc_light = null;
        int newIntensity, oldIntensity = 0;
        LogicTuple t;

        try {
            tc_intensity =
                    new TucsonTupleCentreId("tc_intensity", "localhost",
                            String.valueOf(20504));
            tc_light =
                    new TucsonTupleCentreId("tc_light", "localhost",
                            String.valueOf(20504));
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (this.iteraction) {
            newIntensity = -1;
            oldIntensity = -1;

            try {
                t = LogicTuple.parse("intensity_to_set(X)");
                newIntensity =
                        acc.inp(tc_intensity, t, null).getLogicTupleResult()
                                .getArg(0).intValue();
            } catch (final Exception e) {
                /*
                 * 
                 */
            }
            try {
                t = LogicTuple.parse("cmd(read(intensity))");
                acc.out(tc_light, t, null);
                t = LogicTuple.parse("intensity_set_to(X)");
                oldIntensity =
                        acc.inp(tc_light, t, null).getLogicTupleResult()
                                .getArg(0).intValue();
            } catch (final Exception e) {
                /*
                 * 
                 */
            }
            try {
                if ((newIntensity != oldIntensity) && (newIntensity >= 0)
                        && (newIntensity <= 100)) {
                    System.out.println("[AG_INTENSITY] New intensity value: "
                            + newIntensity);
                    t =
                            LogicTuple.parse("cmd(set(intensity,"
                                    + newIntensity + "))");
                    acc.out(tc_light, t, null);
                }
            } catch (final Exception e) {
                // System.err.println(e.toString());
            }
        }

    }

}
