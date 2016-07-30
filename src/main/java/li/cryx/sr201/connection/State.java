package li.cryx.sr201.connection;

import javax.swing.Icon;

import li.cryx.sr201.util.IconSupport;
import li.cryx.sr201.util.Icons;

/**
 * This enum represents the state of a relay.
 *
 * @author cryxli
 */
public enum State {

	/** Indicator that the state of a relay is not known. */
	UNKNOWN('.', '.', Icons.UNKNOWN.resource()),
	/** Relay is off or released */
	OFF('0', '2', Icons.OFF.resource()),
	/** Relay is on or pulled */
	ON('1', '1', Icons.ON.resource());

	public static State valueOfReceived(final byte b) {
		for (State s : values()) {
			if (s.receive == b) {
				return s;
			}
		}
		return UNKNOWN;
	}

	public static State valueOfSend(final byte b) {
		for (State s : values()) {
			if (s.send == b) {
				return s;
			}
		}
		return UNKNOWN;
	}

	private final byte receive;

	private final byte send;

	private final Icon icon;

	private State(final char receive, final char send, final String iconResource) {
		this.receive = (byte) receive;
		this.send = (byte) send;
		icon = IconSupport.getIcon(iconResource);
	}

	public Icon icon() {
		return icon;
	}

	/** Get the byte that is received and corresponds to this state. */
	public byte receive() {
		return receive;
	}

	/** Get the byte that must be sent to switch to the current state. */
	public byte send() {
		return send;
	}
}
