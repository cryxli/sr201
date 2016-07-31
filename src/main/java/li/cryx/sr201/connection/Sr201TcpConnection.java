package li.cryx.sr201.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import li.cryx.sr201.util.Closer;

/**
 * TCP implementation of a {@link Sr201Connection}.
 *
 * <p>
 * Only when connected over TCP the board will answer on any command with its
 * current state. But it only supports 6 continuous connections. Therefore, a
 * connection is closed after 15 seconds after the last command was received.
 * The manufacturer suggests to frequently send state queries to keep the
 * connection alive.
 * </p>
 *
 * <p>
 * Since only over TCP the board answers, only over TCP the command to query the
 * relay states is implemented. Sending the two ASCII character
 * "<code>00</code>" which is the same as <code>48</code>, <code>48</code> in
 * decimal, will make the board to answer with its current state without
 * changing anything.
 * </p>
 *
 * <p>
 * The board will always answer with exactly 8 bytes after a successful command.
 * Each byte indicating the state of a relay. Off or released state is
 * represented by the decimal value <code>48</code> which is when turned into
 * US_ASCII the value of the <code>0</code> character. On or pulled state is
 * <code>49</code> decimal or <code>1</code> as a character.
 * </p>
 *
 * @author cryxli
 */
class Sr201TcpConnection extends AbstractSr201Connection {

	/** TCP Port cannot be changed. Fixed to <code>6722</code>. */
	private static final int PORT = 6722;

	/** IP address of the relay. */
	private final String ip;

	/** Connection to the relay. */
	private Socket socket;

	/** Stream to send data */
	private OutputStream out;

	/** Stream to receive data */
	private InputStream in;

	public Sr201TcpConnection(final String ip) {
		this.ip = ip;
	}

	@Override
	public void close() {
		if (isConnected()) {
			Closer.close(socket);
			socket = null;
			out = null;
			in = null;
		}
	}

	@Override
	public void connect() throws ConnectionException {
		try {
			socket = SocketFactory.newSocket(ip, PORT);
			out = socket.getOutputStream();
			in = socket.getInputStream();
		} catch (final IOException e) {
			close();
			throw new ConnectionException("msg.tcp.cannot.connect", e, ip, PORT);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
	}

	@Override
	public byte[] getStateBytes() throws ConnectionException {
		return send("00".getBytes(StandardCharsets.US_ASCII));
	}

	@Override
	public boolean isConnected() {
		return socket != null;
	}

	@Override
	public byte[] send(final byte[] data) throws ConnectionException {
		if (!isConnected()) {
			// we have not yet connected
			connect();
		}

		try {
			out.write(data);
			out.flush();
		} catch (final IOException e) {
			throw new ConnectionException("msg.tcp.cannot.send", e);
		}

		try {
			final byte[] buf = new byte[1024];
			final int len = in.read(buf);
			if (len >= 0) {
				return Arrays.copyOf(buf, len);
			} else {
				// detect disconnect by peer
				close();
				throw new DisconnectedException();
			}
		} catch (final IOException e) {
			throw new ConnectionException("msg.tcp.cannot.receive", e);
		}
	}

}
