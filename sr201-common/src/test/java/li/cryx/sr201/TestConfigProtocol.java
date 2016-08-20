package li.cryx.sr201;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import li.cryx.sr201.config.BoardState;
import li.cryx.sr201.config.Sr201Command;
import li.cryx.sr201.util.Closer;

/**
 * This test will try to connect to the SR-201 board and disable cloud service
 * and persistent relay state.
 *
 * @author cryxli
 */
@Ignore
public class TestConfigProtocol {

	/** IP address of board */
	private static final String IP_ADDRESS = "192.168.0.201";

	/** Default gateway */
	private static final String DNS_SERVER = "192.168.1.1";

	/** Password of cloud service */
	private static final String CLOUD_PASSWORD = "000000";

	private Socket socket;

	private OutputStreamWriter osw;

	private InputStreamReader isr;

	private int run;

	@Before
	public void connect() throws IOException {
		// connect to board
		socket = new Socket(IP_ADDRESS, Sr201Command.CONFIG_PORT);
		osw = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII);
		isr = new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII);

		// prepare a random sequence for the commands
		run = 1000 + new Random().nextInt(9000);
	}

	/**
	 * Test to disable cloud service. No error handling.
	 */
	@Test
	public void disableCloudTest() throws IOException {
		// get current config of board
		sendCommand(Sr201Command.QUERY_STATE.cmd(run));
		final BoardState state = BoardState.parseState(readAnswer());
		System.out.println(state);

		// only disable cloud service, if it is on
		if (state.isCloudServiceEnabled()) {
			// it is possible to send multiple commands one after another
			sendCommand(Sr201Command.CLOUD_DISABLE.cmd(run));
			if (!receiveOk()) {
				System.out.println("failed to disable Cloud service");
				return;
			}
			sendCommand(Sr201Command.SET_DNS.cmd(run, DNS_SERVER));
			if (!receiveOk()) {
				System.out.println("failed to set DNS server");
				return;
			}
			sendCommand(Sr201Command.SET_PW.cmd(run, CLOUD_PASSWORD));
			if (!receiveOk()) {
				System.out.println("failed to set password");
				return;
			}
			sendCommand(Sr201Command.RESTART.cmd(run));
			if (!receiveOk()) {
				System.out.println("failed to restart board");
				return;
			}
		}
	}

	/**
	 * Test to disable persistent states. No error handling.
	 */
	@Test
	public void disablePersistentTest() throws IOException {
		// get current config of board
		sendCommand(Sr201Command.QUERY_STATE.cmd(run));
		final BoardState state = BoardState.parseState(readAnswer());
		System.out.println(state);

		// only disable persistent states, if they are on
		if (state.isPersistent()) {
			sendCommand(Sr201Command.STATE_TEMPORARY.cmd(run));
			if (!receiveOk()) {
				System.out.println("failed to reset persistent state");
				return;
			}
			sendCommand(Sr201Command.RESTART.cmd(run));
			if (!receiveOk()) {
				System.out.println("failed to restart board");
				return;
			}
		}
	}

	@After
	public void disconnect() {
		Closer.close(socket);
	}

	/**
	 * Read answer of board.
	 *
	 * @return String version of the byte[] answer after converting using
	 *         US_ASCII.
	 * @throws IOException
	 *             is thrown, if an error occurred reading from the socket.
	 */
	private String readAnswer() throws IOException {
		// read from socket
		final char[] chars = new char[1024];
		final int len = isr.read(chars);
		// turn into a string
		final String s = String.copyValueOf(chars, 0, len);
		// log received answer
		System.out.println("<<< " + s);
		// done
		return s;
	}

	/**
	 * Read and check answer from board.
	 *
	 * @return <code>true</code>, if the board answered with an OK message,
	 *         <code> false</code> otherwise.
	 * @throws IOException
	 *             is thrown, if an error occurred reading from the socket.
	 */
	private boolean receiveOk() throws IOException {
		// read answer
		final String s = readAnswer();
		// expect it to be an OK answer
		return ">OK;".equals(s);
	}

	/**
	 * Send the given command to the board.
	 *
	 * @param cmd
	 *            String version of a command for the board. Will be turned into
	 *            bytes using US_ASCII.
	 * @throws IOException
	 *             is thrown, if an error occurred writing to the socket.
	 */
	private void sendCommand(final String cmd) throws IOException {
		// log command
		System.out.println(">>> " + cmd);
		// send command
		osw.write(cmd);
		osw.flush();
	}

}
