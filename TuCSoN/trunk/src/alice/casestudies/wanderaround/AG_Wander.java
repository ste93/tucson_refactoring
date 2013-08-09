package alice.casestudies.wanderaround;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class AG_Wander extends AbstractTucsonAgent {

    private boolean iteraction = true;

    public AG_Wander(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tc_avoid = null;
        int angle, speed;
        LogicTuple t;

        try {
            tc_avoid =
                    new TucsonTupleCentreId("tc_avoid", "localhost", "" + 20504);
        } catch (final TucsonInvalidTupleCentreIdException e1) {
            e1.printStackTrace();
        }

        while (this.iteraction) {
            try {
                angle = (int) (Math.random() * 360);
                speed = (int) (Math.random() * 100);
                t =
                        LogicTuple.parse("data(random_direction(" + angle + ","
                                + speed + "))");
                acc.out(tc_avoid, t, null);
            } catch (final Exception e) {
                System.err.println(e.toString());
            }
        }

    }

}
