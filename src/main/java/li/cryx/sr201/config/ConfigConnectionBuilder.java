package li.cryx.sr201.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import li.cryx.sr201.connection.ConnectionException;
import li.cryx.sr201.connection.DisconnectedException;
import li.cryx.sr201.util.Closer;

/**
 * This class abstracts handling of the config commands.
 * <p>
 * The methods usually support chaining. This will execute multiple commands at
 * once, once {@link #send()} is called.
 * </p>
 * <p>
 * The {@link #readConfig()} method will query the internal config of the board
 * immediately and wipe all pending commands.
 * </p>
 *
 * @author cryxli
 */
public class ConfigConnectionBuilder {

	/** Internal data structure to have commands and their error combined */
	private static class Command {
		/** Resolved command string. */
		public String cmd;
		/** Language key to be displayed in case of an error */
		public String error;

		public Command(final String cmd, final String error) {
			this.cmd = cmd;
			this.error = error;
		}
	}

	/** Comparator to sort {@link Command}s. */
	private static class CommandComparator implements Comparator<Command> {
		@Override
		public int compare(final Command c1, final Command c2) {
			return c1.cmd.compareTo(c2.cmd);
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(ConfigConnectionBuilder.class);

	/** Current IP address of the board. */
	private final String ip;

	/** Sequence for the current commands. */
	private int run;

	/** Connection to the board. */
	private Socket socket;

	/** Receiving data */
	private Reader in;

	/** Sending data. */
	private Writer out;

	/**
	 * List of pending commands. Will be executed once {@link #send()} is
	 * called.
	 */
	private final List<Command> cmds = new LinkedList<Command>();

	/**
	 * Create a new builder that will send commands to the given IP address.
	 * 
	 * @param ip
	 *            Current IP address of board.
	 */
	public ConfigConnectionBuilder(final String ip) {
		this.ip = ip;
		newRun();
	}

	/** Close connection, release resources and reset internal state. */
	private void close() {
		if (socket != null) {
			Closer.close(socket);
			in = null;
			out = null;
			// just in case the user recycles this instance
			cmds.clear();
			newRun();
		}
	}

	/**
	 * Change the Cloud service host.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param cloudHost
	 *            New host.
	 */
	public ConfigConnectionBuilder cloudHost(final String cloudHost) {
		cmds.add(new Command(Sr201Command.SET_HOST.cmd(run, cloudHost), "msg.conf.err.host"));
		return this;
	}

	/**
	 * Open connection to board using the given IP address.
	 *
	 * @throws ConnectionException
	 *             is thrown whenever an IOException is detected. In that case
	 *             the connection is closed again.
	 */
	private void connect() throws ConnectionException {
		try {
			socket = new Socket(ip, Sr201Command.CONFIG_PORT);
			in = new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII);
			out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII);
		} catch (final IOException e) {
			close();
			throw new ConnectionException("msg.conf.cannot.connect");
		}
	}

	/**
	 * Change the board's DNS server.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param dns
	 *            New DNS server.
	 */
	public ConfigConnectionBuilder dns(final String dns) {
		cmds.add(new Command(Sr201Command.SET_DNS.cmd(run, dns), "msg.conf.err.dns"));
		return this;
	}

	/**
	 * Set whether the board should connect to the cloud service.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param enabled
	 *            <code>true</code> will enable the service, <code>false</code>
	 *            will disable it.
	 */
	public ConfigConnectionBuilder enableCloudService(final boolean enabled) {
		if (enabled) {
			cmds.add(new Command(Sr201Command.CLOUD_ENABLE.cmd(run), "msg.conf.err.cloud"));
		} else {
			cmds.add(new Command(Sr201Command.CLOUD_DISABLE.cmd(run), "msg.conf.err.cloud"));
		}
		return this;
	}

	/**
	 * Change the default gateway.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param ip
	 *            New default gateway.
	 */
	public ConfigConnectionBuilder gateway(final String gateway) {
		cmds.add(new Command(Sr201Command.SET_GATEWAY.cmd(run, gateway), "msg.conf.err.gateway"));
		return this;
	}

	/**
	 * Indicator whether are are pending commands.
	 *
	 * @return <code>true</code>, if there is at least one command,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasPendingCommands() {
		return cmds.size() > 0;
	}

	/**
	 * Change the board's IP address.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param ip
	 *            New IP address.
	 */
	public ConfigConnectionBuilder ip(final String ip) {
		cmds.add(new Command(Sr201Command.SET_IP.cmd(run, ip), "msg.conf.err.ip"));
		return this;
	}

	/** Create a new random sequence number. */
	private void newRun() {
		run = 1000 + new Random().nextInt(9000);
	}

	/**
	 * Change the cloud service password.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param password
	 *            New password.
	 */
	public ConfigConnectionBuilder password(final String password) {
		cmds.add(new Command(Sr201Command.SET_PW.cmd(run, password), "msg.conf.err.password"));
		return this;
	}

	/**
	 * Set whether the board should persist the relay states when power is lost,
	 * and restore it after power is back.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param enabled
	 *            <code>true</code> will enable the feature, <code>false</code>
	 *            will disable it.
	 */
	public ConfigConnectionBuilder persistRelayState(final boolean persistent) {
		if (persistent) {
			cmds.add(new Command(Sr201Command.STATE_PERSISTENT.cmd(run), "msg.conf.err.persist"));
		} else {
			cmds.add(new Command(Sr201Command.STATE_TEMPORARY.cmd(run), "msg.conf.err.persist"));
		}
		return this;
	}

	/**
	 * Ask board for its current config.
	 *
	 * @return Parsed config.
	 * @throws ConnectionException
	 *             is thrown when a communication error occurs.
	 */
	public BoardState readConfig() throws ConnectionException {
		connect();
		final String configString = send(Sr201Command.QUERY_STATE.cmd());
		close();
		return BoardState.parseState(configString);
	}

	/**
	 * Send all pending commands.
	 *
	 * @throws ConnectionException
	 *             Sending is aborted, if any communication error is detected.
	 *             It is also aborted, if the board does not accept a command.
	 */
	public void send() throws ConnectionException {
		if (cmds.size() == 0) {
			// nothing to do
			return;
		}
		// send commands in order
		Collections.sort(cmds, new CommandComparator());
		// add the restart/finished command
		cmds.add(new Command(Sr201Command.RESTART.cmd(run), "msg.conf.err.restart"));

		connect();
		// handshake by reading state
		send(Sr201Command.QUERY_STATE.cmd(run));
		// then execute commands
		for (final Command command : cmds) {
			// one command at a time
			final String answer = send(command.cmd);
			// check answer
			if (!">OK;".equals(answer)) {
				// board rejects command, abort
				if (command.error != null) {
					throw new ConnectionException(command.error);
				} else {
					break;
				}
			}
		}
		close();
	}

	/**
	 * Send a command and wait for the answer from the board.
	 *
	 * @param cmd
	 *            A config command.
	 * @return Received answer from board.
	 * @throws ConnectionException
	 *             is thrown when a communication error occurs.
	 */
	private String send(final String cmd) throws ConnectionException {
		try {
			LOG.info(">>> " + cmd);
			out.write(cmd);
			out.flush();
		} catch (final IOException e) {
			throw new ConnectionException("msg.conf.cannot.send", e);
		}

		try {
			final char[] buf = new char[1024];
			final int len = in.read(buf);
			if (len >= 0) {
				final String s = String.copyValueOf(buf, 0, len);
				LOG.info("<<< " + s);
				return s;
			} else {
				throw new DisconnectedException();
			}
		} catch (final IOException e) {
			throw new ConnectionException("msg.conf.cannot.receive", e);
		}
	}

	/**
	 * Change the board's subnet mask.
	 * <p>
	 * This command is staged and only takes effect, with the next
	 * {@link #send()} method.
	 * </p>
	 *
	 * @param ip
	 *            New subnet mask.
	 */
	public ConfigConnectionBuilder subnet(final String subnet) {
		cmds.add(new Command(Sr201Command.SET_SUBNET.cmd(run, subnet), "msg.conf.err.subnet"));
		return this;
	}

}
