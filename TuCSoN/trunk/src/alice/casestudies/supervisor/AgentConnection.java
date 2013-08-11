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

public class AgentConnection extends AbstractTucsonAgent {
    
    private static final String DEFAULT_PORT = "20504";

    private boolean iteraction = true;

    public AgentConnection(final String aid) throws TucsonInvalidAgentIdException {
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
        TucsonTupleCentreId tcConnection = null;
        TucsonTupleCentreId tcTimer = null;
        boolean connectionStatus = false;
        int nTry = 3;
        LogicTuple t;

        try {
            tcConnection =
                    new TucsonTupleCentreId("tc_connection", "localhost",
                            DEFAULT_PORT);
            tcTimer =
                    new TucsonTupleCentreId("tc_timer", "localhost",
                            DEFAULT_PORT);
        } catch (final TucsonInvalidTupleCentreIdException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (this.iteraction) {
            try {
                t = LogicTuple.parse("cmd(check_connection)");
                acc.out(tcConnection, t, null);
                t = LogicTuple.parse("connection_status(X)");
                t = acc.inp(tcConnection, t, null).getLogicTupleResult();

                if ("ok".equals(t.getArg(0).toString())) {
                    t = LogicTuple.parse("cmd(notify_status(1))");
                    acc.out(tcConnection, t, null);
                    nTry = 3;
                    if (!connectionStatus) {
                        t = LogicTuple.parse("cmd(reset)");
                        acc.out(tcTimer, t, null);
                        connectionStatus = true;
                    }
                } else {
                    nTry--;
                }

                if (nTry == 0) {
                    t = LogicTuple.parse("cmd(notify_status(0))");
                    acc.out(tcConnection, t, null);
                    nTry = 3;
                    if (connectionStatus) {
                        t = LogicTuple.parse("cmd(reset)");
                        acc.out(tcTimer, t, null);
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
