package li.cryx.sr201.connection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Verify basic functionality of {@link Sr201TcpConnection}.
 *
 * @author cryxli
 */
public class Sr201TcpConnectionTest {

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
	private Sr201TcpConnection conn;

	private void assertBytes(final String expected, final byte[] actual) {
		Assert.assertEquals(expected, new String(actual, StandardCharsets.US_ASCII));
	}

	@Before
	public void createConnection() {
		// create new mock
		socketProvider.setSocket(Mockito.mock(Socket.class));
		// create new connection
		conn = new Sr201TcpConnection("192.168.1.100");
	}

	/**
	 * Verify that the connection does clear it internal state. This is only
	 * visible as the {@link Sr201Connection#isConnected()} value.
	 */
	@Test
	public void testClose() throws IOException {
		// test - never connected
		Assert.assertFalse(conn.isConnected());
		conn.close();
		Assert.assertFalse(conn.isConnected());
		// verify
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();

		// test - connect first, then disconnect
		conn.connect();
		Assert.assertTrue(conn.isConnected());
		conn.close();
		Assert.assertFalse(conn.isConnected());
		// verify
		Mockito.verify(socketProvider.getSocket()).close();
	}

	/**
	 * Verify that the connection prepares itself properly.
	 */
	@Test
	public void testConnect() throws IOException {
		// test
		Assert.assertFalse(conn.isConnected());
		conn.connect();
		Assert.assertTrue(conn.isConnected());

		// verify
		Assert.assertEquals("192.168.1.100", socketProvider.getLastHost());
		Assert.assertEquals(6722, socketProvider.getLastPort());
		Mockito.verify(socketProvider.getSocket()).getInputStream();
		Mockito.verify(socketProvider.getSocket()).getOutputStream();
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();
	}

	@Test
	public void testGetStateBytes() throws IOException {
		// prepare
		final ByteArrayInputStream is = new ByteArrayInputStream("000000".getBytes(StandardCharsets.US_ASCII));
		Mockito.when(socketProvider.getSocket().getInputStream()).thenReturn(is);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		Mockito.when(socketProvider.getSocket().getOutputStream()).thenReturn(os);
		// test
		Assert.assertFalse(conn.isConnected());
		assertBytes("000000", conn.getStateBytes());
		Assert.assertTrue(conn.isConnected());
		// verify
		assertBytes("00", os.toByteArray());
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();
	}

	@Test
	public void testGetStates() throws IOException {
		// prepare
		final ByteArrayInputStream is = new ByteArrayInputStream("000000".getBytes(StandardCharsets.US_ASCII));
		Mockito.when(socketProvider.getSocket().getInputStream()).thenReturn(is);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		Mockito.when(socketProvider.getSocket().getOutputStream()).thenReturn(os);
		// test
		Assert.assertFalse(conn.isConnected());
		Assert.assertEquals("000000", conn.getStates());
		Assert.assertTrue(conn.isConnected());
		// verify
		assertBytes("00", os.toByteArray());
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();
	}

	@Test
	public void testMultipleSends() throws IOException {
		// prepare - have input stream deliver two different streams
		final InputStream is = Mockito.mock(InputStream.class);
		Mockito.when(is.read(Matchers.any(byte[].class))).then(new Answer<Integer>() {
			int counter = 0;

			@Override
			public Integer answer(final InvocationOnMock invocation) throws Throwable {
				final byte[] buffer = invocation.getArgumentAt(0, byte[].class);
				buffer[0] = (byte) (48 + counter % 2);
				buffer[1] = 48;
				buffer[2] = 48;
				buffer[3] = 48;
				buffer[4] = 48;
				buffer[5] = 48;
				counter++;
				return 6;
			}
		});
		Mockito.when(socketProvider.getSocket().getInputStream()).thenReturn(is);
		// prepare - ByteArrayOutputStream can be reset
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		Mockito.when(socketProvider.getSocket().getOutputStream()).thenReturn(os);

		// test - first part
		Assert.assertFalse(conn.isConnected());
		Assert.assertEquals("000000", conn.getStates());
		// verify - first part
		Mockito.verify(is).read(Matchers.any(byte[].class));
		assertBytes("00", os.toByteArray());
		os.reset();
		// test - second part
		Assert.assertTrue(conn.isConnected());
		Assert.assertEquals("100000", conn.send("11"));
		Assert.assertTrue(conn.isConnected());
		// verify - second part
		assertBytes("11", os.toByteArray());
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();
		Mockito.verify(is, Mockito.times(2)).read(Matchers.any(byte[].class));
	}

	@Test
	public void testSendBytes() throws IOException {
		// prepare
		final ByteArrayInputStream is = new ByteArrayInputStream("111111".getBytes(StandardCharsets.US_ASCII));
		Mockito.when(socketProvider.getSocket().getInputStream()).thenReturn(is);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		Mockito.when(socketProvider.getSocket().getOutputStream()).thenReturn(os);
		// test
		Assert.assertFalse(conn.isConnected());
		assertBytes("111111", conn.send("1X:10".getBytes(StandardCharsets.US_ASCII)));
		Assert.assertTrue(conn.isConnected());
		// verify
		assertBytes("1X:10", os.toByteArray());
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();
	}

	@Test
	public void testSendString() throws IOException {
		// prepare
		final ByteArrayInputStream is = new ByteArrayInputStream("111111".getBytes(StandardCharsets.US_ASCII));
		Mockito.when(socketProvider.getSocket().getInputStream()).thenReturn(is);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		Mockito.when(socketProvider.getSocket().getOutputStream()).thenReturn(os);
		// test
		Assert.assertFalse(conn.isConnected());
		Assert.assertEquals("111111", conn.send("1X:10"));
		Assert.assertTrue(conn.isConnected());
		// verify
		assertBytes("1X:10", os.toByteArray());
		Mockito.verify(socketProvider.getSocket(), Mockito.never()).close();
	}

}
