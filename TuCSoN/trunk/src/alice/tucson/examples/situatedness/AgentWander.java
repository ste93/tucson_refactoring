package alice.tucson.examples.situatedness;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class AgentWander extends AbstractTucsonAgent {

    private static final String DEFAULT_PORT = "20504";

    private boolean iteraction = true;

    public AgentWander(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tcAvoid = null;
        int angle, speed;
        LogicTuple t;

        try {
            tcAvoid =
                    new TucsonTupleCentreId("tc_avoid", "localhost",
                            DEFAULT_PORT);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                angle = (int) (Math.random() * 360);
                speed = (int) (Math.random() * 100);
                t =
                        LogicTuple.parse("data(random_direction(" + angle + ","
                                + speed + "))");
                acc.out(tcAvoid, t, null);
                Thread.sleep(5000);
            } catch (final Exception e) {
                System.err.println(e.toString());
            }
        }

    }

}
