package alice.tucson.network.test;

import java.net.InetAddress;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.network.AbstractTucsonProtocol;
import alice.tucson.network.TPConfig;
import alice.tucson.network.TPFactory;
import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocolTCP;

/**
 * This is a simple test for check the dialog. A JUnit test with multiple thread
 * requires additional libraries
 * 
 * @author Saverio Cicora
 * 
 */
public final class TPTestDialog {

    public static void main(final String[] args) {
        TPTestDialog.startNode();
        TPTestDialog.startAgent();
    }

    public static void startAgent() {
        (new Thread() {
            @Override
            public void run() {
                TPTestDialog.agent();
            }
        }).start();
    }

    public static void startNode() {
        (new Thread() {
            @Override
            public void run() {
                TPTestDialog.node();
            }
        }).start();
    }

    private TPTestDialog() {
        super();
    }

    private static void agent() {
        try {
            final int port = TPConfig.getInstance().getNodeTcpPort();
            final TucsonTupleCentreId tid =
                    new TucsonTupleCentreId("foo", InetAddress
                            .getLoopbackAddress().getHostAddress(),
                            String.valueOf(port));

            final AbstractTucsonProtocol agent =
                    TPFactory.getDialogAgentSide(tid);
            if (!(agent instanceof TucsonProtocolTCP)) {
                System.out.println("Error - 1 ");
            }

            for (long i = 0; i < 1000; i++) {

                final LogicTuple lt = new LogicTuple("logictupletest");

                final long time1 = System.nanoTime();
                agent.sendMsgRequest(new TucsonMsgRequest(i, 999, "foo", lt));
                final TucsonMsgReply mr = agent.receiveMsgReply();
                final long time2 = System.nanoTime();

                final float result = (float) (time2 - time1) / 1000000;
                System.out.println("Agent - time in millisecond: " + result);

                if (mr.getId() != i) {
                    System.out.println("Error reply " + i);
                }
                if (mr.getType() != 100) {
                    System.out.println("Error reply " + i);
                }
                if (!(mr.isAllowed() && mr.isResultSuccess() && mr.isSuccess())) {
                    System.out.println("Error reply " + i);
                }

            }
            agent.sendMsgRequest(new TucsonMsgRequest(1000L, 0, "foo",
                    new LogicTuple("logictupletest")));
            agent.end();
        } catch (final Exception e) {
            System.out.println("Agent Error");
            e.printStackTrace();
        }
    }

    private static void node() {
        try {

            final AbstractTucsonProtocol node = TPFactory.getDialogNodeSide();
            final AbstractTucsonProtocol dialog = node.acceptNewDialog();

            Thread.sleep(200);
            long i = 0;
            boolean cond = true;
            while (cond) {
                try {
                    final TucsonMsgRequest m = dialog.receiveMsgRequest();
                    if (m.getType() == 0) {
                        cond = false;
                        System.out.println("Nodo: termino");
                    } else {
                        if (m.getId() != i) {
                            System.out.println("Error ID");
                        }
                        if (!("foo".equals(m.getTid()))) {
                            System.out.println("Error TID");
                        }
                        if (!("logictupletest".equals(m.getTuple().getName()))) {
                            System.out.println("Error LogicTuple");
                        }
                        if (m.getType() != 999) {
                            System.out.println("Error MSG type");
                        }

                        dialog.sendMsgReply(new TucsonMsgReply(i, 100, true,
                                true, true));
                        System.out.println("Received message whit ID: "
                                + m.getId());
                        i++;
                    }
                } catch (final Exception e) {
                    break;
                }
            }

            dialog.end();
            node.end();

        } catch (final Exception e) {
            /*
             * 
             */
        }
    }
}
