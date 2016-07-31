package li.cryx.sr201.connection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Since there is no easy way to intercept the low level communication, this
 * factory allows to replace socket implementation completely.
 *
 * @author cryxli
 */
public class SocketFactory {

	/**
	 * Definition of a provider for socket implementations.
	 *
	 * @author cryxli
	 */
	public interface SocketProvider {

		/** Create a new instance of a UDP socket. */
		DatagramSocket newDatagramSocket() throws SocketException;

		/** Create a new instance of a TCP socket. */
		Socket newSocket(String host, int port) throws UnknownHostException, IOException;

	}

	/**
	 * Default implementation that just returns Java's default sockets.
	 */
	private static final SocketProvider DEFAULT_SOCKET_PROVIDER = new SocketProvider() {

		@Override
		public DatagramSocket newDatagramSocket() throws SocketException {
			return new DatagramSocket();
		}

		@Override
		public Socket newSocket(final String host, final int port) throws UnknownHostException, IOException {
			return new Socket(host, port);
		}

	};

	/**
	 * Current socket provider. Defaults to Java's implementation from
	 * <code>java.net</code> package.
	 */
	private static SocketProvider provider = DEFAULT_SOCKET_PROVIDER;

	/** Replace socket factory with the provided one. */
	public static void changeSocketProvider(final SocketProvider socketProvider) {
		if (socketProvider != null) {
			provider = socketProvider;
		}
	}

	/** Get a new instance of a UDP DatagramSocket. */
	public static DatagramSocket newDatagramSocket() throws SocketException {
		return provider.newDatagramSocket();
	}

	/** Get a new instance of a TCP Socket. */
	public static Socket newSocket(final String host, final int port) throws UnknownHostException, IOException {
		return provider.newSocket(host, port);
	}

	/** Revert to using Java sockets. */
	public static void useDefaultSockets() {
		provider = DEFAULT_SOCKET_PROVIDER;
	}

	private SocketFactory() {
		// static stingleton
	}

}
