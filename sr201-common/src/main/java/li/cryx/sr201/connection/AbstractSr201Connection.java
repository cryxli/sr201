package li.cryx.sr201.connection;

import java.nio.charset.StandardCharsets;

/**
 * Methods common to TCP and UDP implementation of {@link Sr201Connection} are
 * implemented in this common super class.
 *
 * <p>
 * Note about design: The implementations are kept low level and therefore
 * operate only on bytes. The same functionality as implemented in this class
 * could be achieved using the connection and wrapping the stream into
 * readers/writers.
 * </p>
 *
 * @author cryxli
 */
abstract class AbstractSr201Connection implements Sr201Connection {

	@Override
	public String getStates() throws ConnectionException {
		// delegate to byte version of this method
		return toString(getStateBytes());
	}

	@Override
	public String send(final String data) throws ConnectionException {
		// delegate to byte version of this method
		final byte[] sendBytes = data.getBytes(StandardCharsets.US_ASCII);
		final byte[] receivedBytes = send(sendBytes);
		return toString(receivedBytes);
	}

	/**
	 * Turn the given byte array into a string.
	 *
	 * @param data
	 * @return String containing the same bytes using US_ASCII
	 */
	private String toString(final byte[] data) {
		if (data != null) {
			return new String(data, StandardCharsets.US_ASCII);
		} else {
			return null;
		}
	}

}
