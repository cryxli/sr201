package li.cryx.sr201.connection;

public enum Channel {
	/** Relay 1 */
	CH1('1'),
	/** Relay 2 */
	CH2('2'),
	/** Relay 3 */
	CH3('3'),
	/** Relay 4 */
	CH4('4'),
	/** Relay 5 */
	CH5('5'),
	/** Relay 6 */
	CH6('6'),
	/** Relay 7 */
	CH7('7'),
	/** Relay 8 */
	CH8('8'),

	/**
	 * Special constant indicating "all relays". Used to switch all relays at
	 * once.
	 */
	ALL('X');

	private final byte key;

	private Channel(final char ch) {
		key = (byte) ch;
	}

	/** Get byte representing this channel. */
	public byte key() {
		return key;
	}
}
