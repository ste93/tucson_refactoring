package alice.tucson.network.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import alice.tucson.api.TucsonTupleCentreId;
import alice.tucson.network.TPConfig;
import alice.tucson.network.TPFactory;
import alice.tucson.network.TucsonProtocol;
import alice.tucson.network.TucsonProtocolTCP;
import alice.tucson.network.exceptions.DialogException;

public class TPJUnitTest {

	/** Reset the singleton at every test */
	@Before
	public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field instance = TPConfig.class.getDeclaredField("singletonTPConfig");
		instance.setAccessible(true);
		instance.set(null, null);
	}

	@Rule
	public ExpectedException exception = ExpectedException.none();

	// TPConfig test --------------------------------------
	@Test
	public void TPConfigGetInstance() {

		TPConfig tp1 = TPConfig.getInstance();
		assertNotNull("Test of singleton", tp1);
		TPConfig tp2 = TPConfig.getInstance();
		assertNotNull("Test of singleton", tp2);

		tp1.setTcpPort(12345);

		assertSame("Test if are the same object", tp1, tp2);
		assertEquals("Test if two object return the same value", tp1.getNodeTcpPort(), tp2.getNodeTcpPort());
	}

	@Test
	public void TPConfigSetGetTcpPort() {
		TPConfig tp = TPConfig.getInstance();

		try {
			tp.setTcpPort(99999);
			fail("An IllegalArgumentException was attended");
		} catch (Exception e) {
			if (!(e instanceof IllegalArgumentException)) {
				fail("The exception is not an IllegalArgumentException");
			}
		}
		assertEquals(20504, tp.getNodeTcpPort());

		try {
			tp.setTcpPort(-1);
			fail("An IllegalArgumentException was attended");
		} catch (Exception e) {
			if (!(e instanceof IllegalArgumentException)) {
				fail("The exception is not an IllegalArgumentException");
			}
		}
		assertEquals(20504, tp.getNodeTcpPort());

		tp.setTcpPort(12345);
		assertEquals(12345, tp.getNodeTcpPort());

		try {
			tp.setTcpPort(5555);
			fail("An IllegalArgumentException was attended");
		} catch (Exception e) {
			if (!(e instanceof IllegalArgumentException)) {
				fail("The exception is not an IllegalArgumentException");
			}
		}
		assertEquals(12345, tp.getNodeTcpPort());

	}

	@Test
	public void TPConfigDefaultValue() {
		TPConfig tp = TPConfig.getInstance();
		tp.setTcpPort(12345);

		assertEquals(20504, tp.getDefaultTcpPort());
		assertEquals(TPFactory.DIALOG_TYPE_TCP, tp.getDefaultProtocolType());
	}

	// TucsonProtocol test --------------------------------------

	@Test
	public void TucsonProtocolNodeSide() throws Exception {
		TucsonProtocol tp1 = TPFactory.getDialogNodeSide();
		assertTrue(tp1 instanceof TucsonProtocolTCP);
		tp1.end();

		TucsonProtocol tp2 = TPFactory.getDialogNodeSide(TPFactory.DIALOG_TYPE_TCP);
		assertTrue(tp2 instanceof TucsonProtocolTCP);
		tp2.end();

		exception.expect(DialogException.class);
		exception.expectMessage("Unsupported protocol type");
		TucsonProtocol tp3 = TPFactory.getDialogNodeSide(100);
		tp3.end();
	}
	
	@Test
	public void TucsonProtocolAgentSide() throws Exception {
		TucsonTupleCentreId tid = new TucsonTupleCentreId("foo", "127.0.0.1", "20504");
		exception.expect(DialogException.class);
		TucsonProtocol tp1 = TPFactory.getDialogAgentSide(tid);
		assertTrue(tp1 instanceof TucsonProtocolTCP);
		tp1.end();

	}

}
