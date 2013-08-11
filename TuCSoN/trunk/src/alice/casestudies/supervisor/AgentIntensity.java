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

public class AgentIntensity extends AbstractTucsonAgent {
    
    private static final String DEFAULT_PORT = "20504";

    private boolean iteraction = true;

    public AgentIntensity(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tcIntensity = null;
        TucsonTupleCentreId tcLight = null;
        int newIntensity, oldIntensity = 0;
        LogicTuple t;

        try {
            tcIntensity =
                    new TucsonTupleCentreId("tc_intensity", "localhost",
                            DEFAULT_PORT);
            tcLight =
                    new TucsonTupleCentreId("tc_light", "localhost",
                            DEFAULT_PORT);
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
                        acc.inp(tcIntensity, t, null).getLogicTupleResult()
                                .getArg(0).intValue();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                t = LogicTuple.parse("cmd(read(intensity))");
                acc.out(tcLight, t, null);
                t = LogicTuple.parse("intensity_set_to(X)");
                oldIntensity =
                        acc.inp(tcLight, t, null).getLogicTupleResult()
                                .getArg(0).intValue();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            try {
                if ((newIntensity != oldIntensity) && (newIntensity >= 0)
                        && (newIntensity <= 100)) {
                    System.out.println("[AG_INTENSITY] New intensity value: "
                            + newIntensity);
                    t =
                            LogicTuple.parse("cmd(set(intensity,"
                                    + newIntensity + "))");
                    acc.out(tcLight, t, null);
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

    }

}
