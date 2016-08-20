package li.cryx.sr201.connection;

import java.io.Closeable;

/**
 * Low level (TCP or UDP) communication channel to the relay.
 *
 * @author cryxli
 */
public interface Sr201Connection extends Closeable {

	/**
	 * Close the connection, if not already close.
	 * <p>
	 * The implementation should not throw <code>IllegalStateException</code>s
	 * when the connection has already been closed.
	 * </p>
	 */
	@Override
	void close();

	/**
	 * Open connection to the relay.
	 *
	 * @throws ConnectionException
	 *             is thrown when the connection could not be established.
	 */
	void connect() throws ConnectionException;

	/**
	 * Query the relay for its state. Only supported by TCP. UDP may emulate
	 * this by returning the last state.
	 *
	 * @return 8 byte array representing the state of each relay as characters
	 *         <code>0</code> or <code>1</code>, or, <code>48</code> or
	 *         <code>49</code> in decimal values.
	 * @throws ConnectionException
	 *             is thrown when the command could not be sent or the answer
	 *             was not received correctly.
	 */
	byte[] getStateBytes() throws ConnectionException;

	/**
	 * Query the relay for its state. Only supported by TCP. UDP may emulate
	 * this by returning the last state.
	 *
	 * @return 8 character string. Each character represents the state of a
	 *         relay as <code>0</code>s or <code>1</code>s.
	 * @throws ConnectionException
	 *             is thrown when the command could not be sent or the answer
	 *             was not received correctly.
	 */
	String getStates() throws ConnectionException;

	/**
	 * Get the state of the connection.
	 *
	 * @return <code>true</code> means that the connection to the relay is open;
	 *         <code>false</code> that there is no connection.
	 */
	boolean isConnected();

	/**
	 * Send the given command to the relay and return its answer.
	 *
	 * @param data
	 *            Arbitrary command sequence understood by the relay.
	 * @return 8 byte array representing the state of each relay as characters
	 *         <code>0</code> or <code>1</code>, or, <code>48</code> or
	 *         <code>49</code> in decimal values.
	 * @throws ConnectionException
	 *             is thrown when the command could not be sent or the answer
	 *             was not received correctly.
	 */
	byte[] send(byte[] data) throws ConnectionException;

	/**
	 * Send the given command to the relay and return its answer.
	 * 
	 * @param data
	 *            Arbitrary command sequence understood by the relay. This
	 *            command will be turned into bytes using US_ASCII before
	 *            sending.
	 * @return 8 character string. Each character represents the state of a
	 *         relay as <code>0</code>s or <code>1</code>s.
	 * @throws ConnectionException
	 *             is thrown when the command could not be sent or the answer
	 *             was not received correctly.
	 */
	String send(String data) throws ConnectionException;

}
