package maraldi.casestudy;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;

public class AgentCollide extends AbstractTucsonAgent {

    private static final String DEFAULT_PORT = "20504";

    private boolean iteraction = true;

    public AgentCollide(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tcMotor = null;
        TucsonTupleCentreId tcSonar = null;
        final SynchACC acc = this.getContext();
        LogicTuple t;

        try {
            tcMotor =
                    new TucsonTupleCentreId("tc_motor", "localhost",
                            DEFAULT_PORT);
            tcSonar =
                    new TucsonTupleCentreId("tc_sonar", "localhost",
                            DEFAULT_PORT);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("distance(front,X)");
                t = acc.rdp(tcSonar, t, null).getLogicTupleResult();

                if (!t.getArg(1).isVar() && t.getArg(1).intValue() < 15) {
                    t = LogicTuple.parse("data(halt)");
                    acc.out(tcMotor, t, null);
                }
                Thread.sleep(1000);
            } catch (final Exception e) {
                System.err.println(e.toString());
            }
        }
    }

}
