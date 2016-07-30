package li.cryx.sr201.connection;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import li.cryx.sr201.Settings;

/**
 * This class offers a higher degree of abstraction to the TCP or UDP
 * implementation of the {@link Sr201Connection}.
 *
 * @author cryxli
 */
public class HighLevelConnection implements Closeable {

	/** Underlying connection. */
	private final Sr201Connection conn;

	/**
	 * Create new instance by wrapping the connection defined in the given
	 * settings.
	 *
	 * @param settings
	 *            Connection settings.
	 */
	public HighLevelConnection(final Settings settings) {
		conn = new ConnectionFactory(settings).getConnection();
	}

	/**
	 * Turn a 8 byte answer into a map of channels and states.
	 *
	 * @param states
	 *            8 byte array representing the state of each relay as
	 *            characters <code>0</code> or <code>1</code>, or,
	 *            <code>48</code> or <code>49</code> in decimal values.
	 * @return Map representing the same relay states.
	 */
	private Map<Channel, State> bytesToMap(final byte[] states) {
		final Map<Channel, State> result = new HashMap<Channel, State>(8);
		result.put(Channel.CH1, State.valueOfReceived(states[0]));
		result.put(Channel.CH2, State.valueOfReceived(states[1]));
		result.put(Channel.CH3, State.valueOfReceived(states[2]));
		result.put(Channel.CH4, State.valueOfReceived(states[3]));
		result.put(Channel.CH5, State.valueOfReceived(states[4]));
		result.put(Channel.CH6, State.valueOfReceived(states[5]));
		result.put(Channel.CH7, State.valueOfReceived(states[6]));
		result.put(Channel.CH8, State.valueOfReceived(states[7]));
		return result;
	}

	/**
	 * Close the underlying connection.
	 *
	 * @see Sr201Connection#close()
	 */
	@Override
	public void close() {
		conn.close();
	}

	/**
	 * Open the wrapped connection.
	 *
	 * @throws ConnectionException
	 *             is thrown if the connection cannot be established.
	 * @see Sr201Connection#connect()
	 */
	public void connect() throws ConnectionException {
		conn.connect();
	}

	/**
	 * Query the relay for their current states.
	 *
	 * @return Map of channels and associated states.
	 * @throws ConnectionException
	 *             is thrown if the query command could not be executed
	 *             correctly.
	 */
	public Map<Channel, State> getStates() throws ConnectionException {
		return bytesToMap(conn.getStateBytes());
	}

	/**
	 * Get the state of the connection.
	 *
	 * @return <code>true</code> means connected, <code>false</code> is not
	 *         connected.
	 * @see Sr201Connection#isConnected()
	 */
	public boolean isConnected() {
		return conn.isConnected();
	}

	/**
	 * Send a state change command to a channel.
	 *
	 * @param channel
	 *            Indicates with relay should switch. Also supports special
	 *            {@link Channel#ALL} to switch all relays at once.
	 * @param state
	 *            Into which state the relay should switch.
	 *            {@link State#UNKNOWN} is not supported and will therefore not
	 *            issue a command.
	 * @return Map of channels and associated states.
	 * @throws ConnectionException
	 *             is thrown if the query command could not be executed
	 *             correctly.
	 */
	public Map<Channel, State> send(final Channel channel, final State state) throws ConnectionException {
		if (channel == null || state == null || state == State.UNKNOWN) {
			// nothing to send
			return null;
		}
		return bytesToMap(conn.send(new byte[] { state.send(), channel.key() }));
	}

}
