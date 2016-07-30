package li.cryx.sr201;

import java.util.Properties;

import li.cryx.sr201.connection.IpAddressValidator;
import li.cryx.sr201.util.PropertiesSupport;

/**
 * Data structure to hold relay end-point settings. Also adds additional methods
 * to read and write settings from/to properties files.
 *
 * @author cryxli
 */
public class Settings {

	/** Property key for IP setting */
	private static final String KEY_IP = "conn.ip";

	/** Property key for protocol setting */
	private static final String KEY_TCP = "conn.protocol";

	/** Indicator that IP is valid */
	private boolean valid = false;

	/** IP address of relay */
	private String ip;

	/** Use TCP or UDP protocol */
	private boolean tcp;

	/** Create settings with default values */
	public Settings() {
		this(new Properties());
	}

	/**
	 * Create settings from given Properties file. Will fall back to default
	 * settings, if a property is missing.
	 */
	public Settings(final Properties prop) {
		final Properties defaultProp = PropertiesSupport.loadFromXmlResource("/config/default.xml");

		if (prop.getProperty(KEY_IP) != null) {
			setIp(prop.getProperty(KEY_IP));
		} else {
			setIp(defaultProp.getProperty(KEY_IP));
		}

		if (prop.getProperty(KEY_TCP) != null) {
			setTcp("TCP".equalsIgnoreCase(prop.getProperty(KEY_TCP)));
		} else {
			setTcp("TCP".equalsIgnoreCase(defaultProp.getProperty(KEY_TCP)));
		}
	}

	public Properties exportProperties() {
		return exportProperties(new Properties());
	}

	public Properties exportProperties(final Properties prop) {
		prop.setProperty(KEY_IP, getIp());
		prop.setProperty(KEY_TCP, isTcp() ? "TCP" : "UDP");
		return prop;
	}

	public String getIp() {
		return ip;
	}

	public boolean isTcp() {
		return tcp;
	}

	public boolean isValid() {
		return valid;
	}

	public void setIp(final String ip) {
		this.ip = ip;
		valid = new IpAddressValidator().isValid(ip);
	}

	public void setTcp(final boolean tcp) {
		this.tcp = tcp;
	}

}
