package li.cryx.sr201.connection;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import li.cryx.sr201.connection.SocketFactory.SocketProvider;

/**
 * special socket provider that lets a unittest replace the sockets on the fly.
 *
 * @author cryxli
 */
public class AbstractSocketProvider implements SocketProvider {

	private Socket socket;

	private DatagramSocket datagramSocket;

	/** Last host */
	private String host;

	/** Last port */
	private int port;

	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}

	public String getLastHost() {
		return host;
	}

	public int getLastPort() {
		return port;
	}

	public Socket getSocket() {
		return socket;
	}

	@Override
	public DatagramSocket newDatagramSocket() throws SocketException {
		return datagramSocket;
	}

	@Override
	public Socket newSocket(final String host, final int port) throws UnknownHostException, IOException {
		this.host = host;
		this.port = port;
		return socket;
	}

	public void setDatagramSocket(final DatagramSocket datagramSocket) {
		this.datagramSocket = datagramSocket;
	}

	public void setSocket(final Socket socket) {
		this.socket = socket;
	}

}
