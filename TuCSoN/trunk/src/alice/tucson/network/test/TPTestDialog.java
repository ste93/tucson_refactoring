package alice.tucson.network.test;

import alice.logictuple.LogicTuple;
import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.network.TPConfig;
import alice.tucson.network.TPFactory;
import alice.tucson.network.TucsonMsgReply;
import alice.tucson.network.TucsonMsgRequest;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;

/**
 * This is a simple test for check the dialog. A JUnit test with multiple thread
 * requires additional libraries
 */
public class TPTestDialog {

	public static void main(String[] args) {
		startNode();
		startAgent();
	}

	private static void agent() {
		try {
			int port = TPConfig.getInstance().getNodeTcpPort();
			TucsonTupleCentreId tid = new TucsonTupleCentreId("foo", "127.0.0.1", "" + port);

			TucsonProtocol agent = TPFactory.getDialogAgentSide(tid);
			if (!(agent instanceof TucsonProtocolTCP)) {
				System.out.println("Error - 1 ");
			}

			for (long i = 0; i < 1000; i++) {

				LogicTuple lt = new LogicTuple("logictupletest");

				long time1 = System.nanoTime();
				agent.sendMsgRequest(new TucsonMsgRequest(i, 999, "foo", lt));
				TucsonMsgReply mr = agent.receiveMsgReply();
				long time2 = System.nanoTime();

				float result = (float) (time2 - time1) / 1000000;
				System.out.println("Agent - time in millisecond: " + result);

				if (mr.getId() != i)
					System.out.println("Error reply " + i);
				if (mr.getType() != 100)
					System.out.println("Error reply " + i);
				if (!(mr.isAllowed() && mr.isResultSuccess() && mr.isSuccess()))
					System.out.println("Error reply " + i);

			}
			agent.sendMsgRequest(new TucsonMsgRequest(1000L, 0, "foo", new LogicTuple("logictupletest")));
			agent.end();
		} catch (Exception e) {
			System.out.println("Agent Error");
			e.printStackTrace();
		}
	}

	private static void node() {
		try {

			TucsonProtocol node = TPFactory.getDialogNodeSide();
			TucsonProtocol dialog = node.acceptNewDialog();

			Thread.sleep(200);
			long i = 0;
			boolean cond = true;
			while (cond) {
				try {
					TucsonMsgRequest m = dialog.receiveMsgRequest();
					if (m.getType() == 0) {
						cond = false;
						System.out.println("Nodo: termino");
					} else {
						if (m.getId() != i)
							System.out.println("Error ID");
						if (!(m.getTid().equals("foo")))
							System.out.println("Error TID");
						if (!(m.getTuple().getName().equals("logictupletest")))
							System.out.println("Error LogicTuple");
						if (m.getType() != 999)
							System.out.println("Error MSG type");

						dialog.sendMsgReply(new TucsonMsgReply(i, 100, true, true, true));
						System.out.println("Received message whit ID: " + m.getId());
						i++;
					}
				} catch (Exception e) {
					break;
				}
			}

			dialog.end();
			node.end();

		} catch (Exception e) {
		}
	}

	public static void startNode() {
		(new Thread() {
			@Override
			public void run() {
				node();
			}
		}).start();
	}

	public static void startAgent() {
		(new Thread() {
			@Override
			public void run() {
				agent();
			}
		}).start();
	}
}
