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
 * Connection monitoring agent ( LIGHT SUPERVISOR CASE STUDY )
 * 
 * @author Steven maraldi
 * 
 */

public class AG_Connection extends AbstractTucsonAgent {

    private boolean iteraction = true;

    public AG_Connection(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tc_connection = null;
        TucsonTupleCentreId tc_timer = null;
        boolean connectionStatus = false;
        int nTry = 3;
        LogicTuple t;

        try {
            tc_connection =
                    new TucsonTupleCentreId("tc_connection", "localhost",
                            String.valueOf(20504));
            tc_timer =
                    new TucsonTupleCentreId("tc_timer", "localhost",
                            String.valueOf(20504));
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("cmd(check_connection)");
                acc.out(tc_connection, t, null);
                t = LogicTuple.parse("connection_status(X)");
                t = acc.inp(tc_connection, t, null).getLogicTupleResult();

                if (t.getArg(0).toString().equals("ok")) {
                    t = LogicTuple.parse("cmd(notify_status(1))");
                    acc.out(tc_connection, t, null);
                    nTry = 3;
                    if (!connectionStatus) {
                        t = LogicTuple.parse("cmd(reset)");
                        acc.out(tc_timer, t, null);
                        connectionStatus = true;
                    }
                } else {
                    nTry--;
                }

                if (nTry == 0) {
                    t = LogicTuple.parse("cmd(notify_status(0))");
                    acc.out(tc_connection, t, null);
                    nTry = 3;
                    if (connectionStatus) {
                        t = LogicTuple.parse("cmd(reset)");
                        acc.out(tc_timer, t, null);
                        connectionStatus = false;
                    }
                }

                Thread.sleep(1000);
            } catch (final Exception e) {
                nTry--;
            }
        }

    }

}
