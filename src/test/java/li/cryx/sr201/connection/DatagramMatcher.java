package li.cryx.sr201.connection;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.mockito.ArgumentMatcher;

/**
 * Since <code>DatagramPacket</code> is an arbitrary java class that it created
 * within the method we want to set, we cannot compare two instances directly.
 * For this case Mockito offers a custom method to compare complex objects using
 * the <code>ArgumentMatcher</code>.
 *
 * <p>
 * This matcher is configured with the expected values that were used to create
 * the <code>DatagramPacket</code> and it will compare them against the values
 * of the received packet.
 * </p>
 *
 * @author cryxli
 */
public class DatagramMatcher extends ArgumentMatcher<DatagramPacket> {

	private final String data;

	private final InetAddress ipAdr;

	private final int port;

	/**
	 * Create a matcher for a <code>DatagramPacket</code> sent to a
	 * <code>DatagramSocket</code>.
	 *
	 * @param data
	 *            Expected sent data. US_ASCII is used to convert the string to
	 *            bytes.
	 * @param ipAdr
	 *            IP address or host name of the target address.
	 * @param port
	 *            Target port.
	 * @throws UnknownHostException
	 *             is thrown, if the <code>ipAdr</code> cannot be resolved to an
	 *             actual IP address by Java.
	 */
	public DatagramMatcher(final String data, final String ipAdr, final int port) throws UnknownHostException {
		this.data = data;
		this.ipAdr = InetAddress.getByName(ipAdr);
		this.port = port;
	}

	@Override
	public boolean matches(final Object argument) {
		final DatagramPacket p = (DatagramPacket) argument;
		return //
		// compare IP addresses
		p.getAddress().equals(ipAdr) //
				// compare ports
				&& p.getPort() == port //
				// and data length
				&& p.getLength() == data.length() //
				// compare data
				&& data.equals(new String(p.getData(), StandardCharsets.US_ASCII));
	}

}
