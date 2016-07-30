package li.cryx.sr201.util;

/**
 * This class centralizes the definition of icons and images.
 *
 * @author cryxli
 */
public enum Icons {

	/** Icon for "test connection" button. */
	TEST("/icon/checked-2-16.png"),
	/** Icon for "connect" button. */
	CONNECT("/icon/checked-1-16.png"),
	/** Icon for "exit application" button. */
	EXIT("/icon/power-button-16.png"),
	/** Icon for "show settings" button. */
	SETTINGS("/icon/wrench-16.png"),

	/** Icon for "unknown relay state" toggle button */
	UNKNOWN("/icon/question-32.png"),
	/** Icon for "relay on state" toggle button */
	ON("/icon/checked-2-32.png"),
	/** Icon for "relay off state" toggle button */
	OFF("/icon/cancel-1-32.png");

	private final String resource;

	private Icons(final String resource) {
		this.resource = resource;
	}

	/** Get the classpath to the image. */
	public String resource() {
		return resource;
	}

}
