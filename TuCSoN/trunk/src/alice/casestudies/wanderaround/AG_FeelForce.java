package alice.casestudies.wanderaround;

import alice.logictuple.LogicTuple;
import alice.tucson.api.AbstractTucsonAgent;
import alice.tucson.api.ITucsonOperation;
import alice.tucson.api.SynchACC;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.api.exceptions.TucsonInvalidAgentIdException;
import alice.tucson.api.exceptions.TucsonInvalidTupleCentreIdException;

public class AG_FeelForce extends AbstractTucsonAgent {

    private int angle, speed;

    private boolean iteraction = true;

    public AG_FeelForce(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tc_sonar = null;
        TucsonTupleCentreId tc_runaway = null;
        TucsonTupleCentreId tc_avoid = null;
        int front, right, back, left;
        LogicTuple t;

        try {
            tc_sonar =
                    new TucsonTupleCentreId("tc_sonar", "localhost",
                            String.valueOf(20504));
            tc_runaway =
                    new TucsonTupleCentreId("tc_runaway", "localhost",
                            String.valueOf(20504));
            tc_avoid =
                    new TucsonTupleCentreId("tc_avoid", "localhost",
                            String.valueOf(20504));
        } catch (final TucsonInvalidTupleCentreIdException e) {
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("distance(front,X)");
                front =
                        acc.rdp(tc_sonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();
                t = LogicTuple.parse("distance(right,X)");
                right =
                        acc.rdp(tc_sonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();
                t = LogicTuple.parse("distance(back,X)");
                back =
                        acc.rdp(tc_sonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();
                t = LogicTuple.parse("distance(left,X)");
                left =
                        acc.rdp(tc_sonar, t, null).getLogicTupleResult()
                                .getArg(1).intValue();

                // System.out.println("[AG_FEELFORCE] F: "+front+" | R: "+right+" | B: "+back+" | L: "+left);
                this.computeDirection(front, right, back, left, this.angle,
                        this.speed);
                // System.out.println("[AG_FEELFORCE] New direction: A: "+angle+" | S: "+speed);

                t =
                        LogicTuple.parse("data(direction(" + this.angle + ","
                                + this.speed + "))");
                acc.out(tc_runaway, t, null);
                acc.out(tc_avoid, t, null);

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
