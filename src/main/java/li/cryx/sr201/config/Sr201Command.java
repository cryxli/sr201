package li.cryx.sr201.config;

import java.util.Random;

/**
 * This enum contains the command sequences that can be sent to the SR-201 board
 * over TCP on port 5111. One command usually changes one parameter. Therefore,
 * commands sent in succession share a common sequence number.
 *
 * <pre>
 * // generate a random 4 digit number
 * int run = 1000 + new Random().nextInt(9000); // 9876
 * // set (Cloud) password to 123456
 * String setPw = Sr201Command.SET_PW.cmd(run, "123456"); // #B9876,123456;
 * // and restart the board within the same run
 * String restart = Sr201Command.RESTART.cmd(run); // #79876;
 * </pre>
 *
 * <p>
 * The board always answers with a string starging with <code>&gt;</code> and
 * ending with <code>;</code>. If the board could execute the command a
 * <code>&gt;OK;</code> is returned. An <code>&gt;ERR;</code>, if it does not
 * know the command.
 * </p>
 *
 * @author cryxli
 */
public enum Sr201Command {

	/**
	 * Ask the board for its current settings.
	 * <p>
	 * The board answers with a comma separated list of its settings.
	 * </p>
	 *
	 * <pre>
	 * >192.168.1.100,255.255.255.0,192.168.1.1,,0,435,F449007D02E2EB000000,192.168.1.1,connect.tutuuu.com,0;
	 * </pre>
	 */
	QUERY_STATE("#1{};"),

	/**
	 * Set the board's IP address. Expects a IPv4 as a string (192.168.1.100) as
	 * an argument.
	 */
	SET_IP("#2{},{};"),

	/**
	 * Set the subnet mask. Expects a IPv4 mask as a string (255.255.255.0) as
	 * an argument.
	 */
	SET_SUBNET("#3{},{};"),

	/**
	 * Set the default gateway used to resolve the cloud service. Expects a IPv4
	 * as a string (192.168.1.1) as an argument.
	 */
	SET_GATEWAY("#4{},{};"),

	// unknown command #5

	/**
	 * Enable persistent relay states when board is powered off and on again.
	 */
	STATE_PERSISTENT("#6{},1;"),

	/**
	 * Disable persistent relay states when board is powered off and on again.
	 */
	STATE_TEMPORARY("#6{},0;"),

	/** Restart the board. Make changes take effect. */
	RESTART("#7{};"),

	/**
	 * Set the DNS server used to resolve the cloud service. Expects a IPv4 as a
	 * string (192.168.1.1) as an argument.
	 */
	SET_DNS("#8{},{};"),

	// unknown command #9

	/** Disable cloud service. */
	CLOUD_DISABLE("#A{},0;"),

	/** Enable cloud service. */
	CLOUD_ENABLE("#A{},1;"),

	/**
	 * Set the password of the cloud service. Expects a 6 character long
	 * password as an argument.
	 */
	SET_PW("#B{},{};");

	public static final int CONFIG_PORT = 5111;

	private final String cmd;

	private Sr201Command(final String cmd) {
		this.cmd = cmd;
	}

	/** Get command with random sequence. */
	public String cmd() {
		return cmd(1000 + new Random().nextInt(9000));
	}

	/** Get command with given sequence. */
	public String cmd(final int run) {
		if (run >= 1000 && run <= 9999) {
			return cmd.replaceFirst("\\{\\}", String.valueOf(run));
		} else {
			return cmd();
		}
	}

	/** Get command with given sequence and data. */
	public String cmd(final int run, final String data) {
		return cmd(run).replaceFirst("\\{\\}", data);
	}

	/** Get command with random sequence and given data. */
	public String cmd(final String data) {
		return cmd(0, data);
	}

}
