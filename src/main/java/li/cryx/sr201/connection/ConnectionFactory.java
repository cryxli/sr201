package li.cryx.sr201.connection;

import li.cryx.sr201.Settings;

/**
 * Factory class to create a {@link Sr201Connection} fitting for the given
 * configuration.
 *
 * @author cryxli
 */
public class ConnectionFactory {

	/** The connection configuration */
	private final Settings settings;

	/** Create a new factory with the given config */
	public ConnectionFactory(final Settings settings) {
		this.settings = settings;
	}

	/**
	 * Create a new connection for the config.
	 *
	 * @return Instance of a {@link Sr201Connection} implementation.
	 * @throws ConnectionException
	 *             is thrown, if the IP address is not valid.
	 */
	public Sr201Connection getConnection() throws ConnectionException {
		if (!settings.isValid()) {
			// validation failed
			throw new ConnectionException("msg.conn.factory.ip.invalid");

		} else if (settings.isTcp()) {
			// TCP case
			return new Sr201TcpConnection(settings.getIp());

		} else {
			// UDP case
			return new Sr201UdpConnection(settings.getIp());
		}
	}

}
