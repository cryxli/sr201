package li.cryx.sr201.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import li.cryx.sr201.util.Closer;

/**
 * UDP implementation of a {@link Sr201Connection}.
 *
 * <p>
 * With UDP there is not open connection to the board. Therefore, opening this
 * connection does not tell whether the board is available. TCP would run into a
 * timeout, if the board did not answer.
 * </p>
 *
 * <p>
 * Over UDP the board does not answer with its state. This implementation tries
 * to keep track of the sent commands ands determines the relay state from them.
 * Not all commands are understood. In addition to the usual <code>0</code> and
 * <code>1</code> answer characters <code>.</code> is used to express
 * uncertainty.
 * </p>
 *
 * @author cryxli
 */
public class Sr201UdpConnection extends AbstractSr201Connection {

	/** UDP Port cannot be changed. Fixed to <code>6723</code>. */
	private static final int PORT = 6723;

	/** IP address of the relay. */
	private final String ip;

	/** Connection to the relay. */
	private DatagramSocket socket;

	/** Validated IP address */
	private InetAddress ipAddress;

	/** Keep track of state changes */
	private byte[] states = "........".getBytes(StandardCharsets.US_ASCII);

	public Sr201UdpConnection(final String ip) {
		this.ip = ip;
	}

	@Override
	public void close() {
		if (isConnected()) {
			Closer.close(socket);
			socket = null;
			ipAddress = null;
		}
	}

	@Override
	public void connect() throws ConnectionException {
		// open socket
		try {
			socket = SocketFactory.newDatagramSocket();
		} catch (final SocketException e) {
			close();
			throw new ConnectionException("msg.udp.cannot.connect", e);
		}
		// validate IP address
		try {
			ipAddress = InetAddress.getByName(ip);
		} catch (final UnknownHostException e) {
			close();
			throw new ConnectionException("msg.udp.cannot.resolve.ip", e, ip);
		}
		// reset internal state
		updateStates(null);
	}

	@Override
	protected void finalize() {
		close();
	}

	@Override
	public byte[] getStateBytes() throws ConnectionException {
		return states;
	}

	@Override
	public boolean isConnected() {
		return socket != null;
	}

	@Override
	public byte[] send(final byte[] data) throws ConnectionException {
		// ensure connection
		if (!isConnected()) {
			connect();
		}

		final DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddress, PORT);
		try {
			socket.send(sendPacket);
		} catch (final IOException e) {
			throw new ConnectionException("msg.udp.cannot.send", e);
		}

		// relay does not answer through UDP
		updateStates(data);

		return states;
	}

	/**
	 * Since the board does not answer with its state, we keep track of the
	 * changes we ordered it to make.
	 *
	 * @param data
	 *            Data bytes of latest command.
	 */
	private void updateStates(final byte[] data) {
		if (data == null || data.length != 2) {
			// unknown command
			states = "........".getBytes(StandardCharsets.US_ASCII);
			return;
		}

		// EBNF:
		// COMMAND := STATE CHANNEL.
		// STATE := ON_STATE | OFF_STATE.
		// CHANNEL := CH1 | CH2 | CH3 | CH4 | CH5 | CH6 | CH7 | CH8 | ALL.
		// ON_STATE := <49 dec, or, "1" in ASCII>.
		// OFF_STATE := <50 dec, or, "2" in ASCII>.
		// CH1 := <49 dec, or, "1" in ASCII>.
		// CH2 := <50 dec, or, "2" in ASCII>.
		// etc.
		// ALL := <88 dec, or "X" in ASCII>.

		// any of the above commands can be executed for a certain amount of
		// seconds only (ASCII only):
		// COMMAND := STATE CHANNEL ":" SECONDS.
		// SECONDS := DIGIT DIGIT.
		// DIGIT := "0" | "1" | ... | "9".
		// but it is difficult to calculate a "resulting" state from it

		// There are two other "extended" commands:
		// COMMAND := STATE CHANNEL "*".
		// COMMAND := STATE CHANNEL "K".
		// which I do not understand

		final int state = data[0];
		final int channel = data[1];

		if (channel >= 49 && channel <= 56) {
			// channel 1 to 8
			if (state == 49) {
				states[channel - 49] = 49;
			} else if (state == 50) {
				states[channel - 49] = 48;
			}
		} else if (channel == 88) {
			// channel X = all relays at once
			if (state == 49) {
				states = "11111111".getBytes(StandardCharsets.US_ASCII);
			} else if (state == 50) {
				states = "00000000".getBytes(StandardCharsets.US_ASCII);
			}
		} else {
			// unknown command, unknown resulting state
			states = "........".getBytes(StandardCharsets.US_ASCII);
		}
	}

}
