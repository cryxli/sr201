package li.cryx.sr201.connection;

import java.io.IOException;
import java.net.DatagramSocket;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Verify datagram interaction of {@link Sr201UdpConnection}.
 *
 * @author cryxli
 */
public class Sr201UdpConnectionTest {

	private static AbstractSocketProvider socketProvider;

	@BeforeClass
	public static void replaceSockets() {
		// intersect socket
		socketProvider = new AbstractSocketProvider();
		SocketFactory.changeSocketProvider(socketProvider);
	}

	@AfterClass
	public static void resetSockets() {
		// reset socket factory
		SocketFactory.useDefaultSockets();
	}

	/** The connection under test */
	private Sr201UdpConnection conn;

	@Before
	public void createConnection() {
		// create new mock
		socketProvider.setDatagramSocket(Mockito.mock(DatagramSocket.class));
		// create new connection
		conn = new Sr201UdpConnection("192.168.1.100");
	}

	@Test
	public void testMultipleSend() throws IOException {
		// test
		Assert.assertEquals("00000000", conn.send("2X"));
		Assert.assertEquals("01000000", conn.send("12"));
		Assert.assertEquals("00000000", conn.send("22"));
		// verify
		Mockito.verify(socketProvider.getDatagramSocket())
				.send(Matchers.argThat(new DatagramMatcher("2X", "192.168.1.100", 6723)));
		Mockito.verify(socketProvider.getDatagramSocket())
				.send(Matchers.argThat(new DatagramMatcher("12", "192.168.1.100", 6723)));
		Mockito.verify(socketProvider.getDatagramSocket())
				.send(Matchers.argThat(new DatagramMatcher("22", "192.168.1.100", 6723)));
	}

	@Test
	public void testSend() throws IOException {
		// test
		Assert.assertEquals(".0......", conn.send("22"));
		// verify
		Mockito.verify(socketProvider.getDatagramSocket())
				.send(Matchers.argThat(new DatagramMatcher("22", "192.168.1.100", 6723)));
	}

}
