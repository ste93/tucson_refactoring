package alice.casestudies.wanderaround;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

public class AG_Collide extends AbstractTucsonAgent {

    private boolean iteraction = true;

    public AG_Collide(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tc_motor = null;
        TucsonTupleCentreId tc_sonar = null;
        final SynchACC acc = this.getContext();
        LogicTuple t;

        try {
            tc_motor =
                    new TucsonTupleCentreId("tc_motor", "localhost",
                            String.valueOf(20504));
            tc_sonar =
                    new TucsonTupleCentreId("tc_sonar", "localhost",
                            String.valueOf(20504));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("distance(front,X)");
                t = acc.rdp(tc_sonar, t, null).getLogicTupleResult();

                if (t.getArg(1).intValue() < 15) {
                    t = LogicTuple.parse("data(halt)");
                    acc.out(tc_motor, t, null);
                }

            } catch (final Exception e) {
                System.err.println(e.toString());
            }
        }
    }

}
