package li.cryx.sr201.connection;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A class to validate IP addresses that were entered as string.
 *
 * @author cryxli
 */
public class IpAddressValidator {

	/**
	 * Validate the given IP address.
	 *
	 * @param ip
	 *            String representation of an IP address.
	 * @return <code>true</code>, if the address is valid.
	 */
	public boolean isValid(final String ip) {
		try {
			// let java do the validation against RFC 2732
			InetAddress.getByName(ip);
			return true;
		} catch (final UnknownHostException e) {
			return false;
		}
	}

}
