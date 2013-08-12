package alice.tucson.examples.situatedness;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class AgentFeelForce extends AbstractTucsonAgent {
    
    private static final String DEFAULT_PORT = "20504";

    private int angle, speed;

    private boolean iteraction = true;

    public AgentFeelForce(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tcSonar = null;
        TucsonTupleCentreId tcRunaway = null;
        TucsonTupleCentreId tcAvoid = null;
        int front, right, back, left;
        LogicTuple t;

        try {
            tcSonar =
                    new TucsonTupleCentreId("tc_sonar", "localhost",
                            DEFAULT_PORT);
            tcRunaway =
                    new TucsonTupleCentreId("tc_runaway", "localhost",
                            DEFAULT_PORT);
            tcAvoid =
                    new TucsonTupleCentreId("tc_avoid", "localhost",
                            DEFAULT_PORT);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("distance(front,X)");
                front =
                        acc.rdp(tcSonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();
                t = LogicTuple.parse("distance(right,X)");
                right =
                        acc.rdp(tcSonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();
                t = LogicTuple.parse("distance(back,X)");
                back =
                        acc.rdp(tcSonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();
                t = LogicTuple.parse("distance(left,X)");
                left =
                        acc.rdp(tcSonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();

                // System.out.println("[AG_FEELFORCE] F: "+front+" | R: "+right+" | B: "+back+" | L: "+left);
                this.computeDirection(front, right, back, left, this.angle,
                        this.speed);
                // System.out.println("[AG_FEELFORCE] New direction: A: "+angle+" | S: "+speed);

                t =
                        LogicTuple.parse("data(direction(" + this.angle + ","
                                + this.speed + "))");
                acc.out(tcRunaway, t, null);
                acc.out(tcAvoid, t, null);

                Thread.sleep(1000);
            } catch (final Exception e) {
                System.err.println(e.toString());
            }
        }

    }

    private void computeDirection(final int front, final int right,
            final int back, final int left, final int a, final int s) {
        // TODO MARALDI: implement some kind of direction computation
        this.angle = (int) (Math.random() * 360);
        this.speed = (int) (Math.random() * 100);
    }

}
