package li.cryx.sr201.config;

/**
 * Data structure to represent the current configuration of a SR-201 board.
 *
 * @author cryxli
 */
public class BoardState {

	public static BoardState parseState(String s) throws IllegalArgumentException {
		if (s == null) {
			throw new IllegalArgumentException("No config sequence given. String must not be null.");
		} else if (!s.startsWith(">")) {
			throw new IllegalArgumentException("Config sequence does not start with an >");
		} else if (!s.endsWith(";")) {
			throw new IllegalArgumentException("Config sequence does not end with an ;");
		}

		s = s.substring(1, s.length() - 1);
		final String[] ss = s.split(",");
		if (ss.length != 10) {
			// unknown answer
			throw new IllegalArgumentException("Unknown number of parameters. Expected 10 is " + ss.length);
		}

		final BoardState state = new BoardState();
		state.ipAddress = ss[0];
		state.subnetMask = ss[1];
		state.gateway = ss[2];
		// unknown "empty"
		state.persistent = "1".equals(ss[4]);
		state.version = "1.0." + ss[5];
		state.serialNumber = ss[6].substring(0, 14);
		state.password = ss[6].substring(14);
		state.dnsServer = ss[7];
		state.cloudService = ss[8];
		state.cloudServiceEnabled = "1".equals(ss[9]);
		return state;
	}

	private String ipAddress;

	private String subnetMask;

	private String gateway;

	private boolean persistent;

	private String version;

	private String serialNumber;

	private String password;

	private String dnsServer;

	private String cloudService;

	private boolean cloudServiceEnabled;

	private BoardState() {
	}

	public String getCloudService() {
		return cloudService;
	}

	public String getDnsServer() {
		return dnsServer;
	}

	public String getGateway() {
		return gateway;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public String getPassword() {
		return password;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public String getSubnetMask() {
		return subnetMask;
	}

	public String getVersion() {
		return version;
	}

	public boolean isCloudServiceEnabled() {
		return cloudServiceEnabled;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setCloudService(final String cloudService) {
		this.cloudService = cloudService;
	}

	public void setCloudServiceEnabled(final boolean cloudServiceEnabled) {
		this.cloudServiceEnabled = cloudServiceEnabled;
	}

	public void setDnsServer(final String dnsServer) {
		this.dnsServer = dnsServer;
	}

	public void setGateway(final String gateway) {
		this.gateway = gateway;
	}

	public void setIpAddress(final String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPersistent(final boolean persistent) {
		this.persistent = persistent;
	}

	public void setSerialNumber(final String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public void setSubnetMask(final String subnetMask) {
		this.subnetMask = subnetMask;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		final StringBuffer buf = new StringBuffer();

		buf.append("IP address ..........: ").append(ipAddress).append('\n');
		buf.append("Subnet mask .........: ").append(subnetMask).append('\n');
		buf.append("Gateway .............: ").append(gateway).append('\n');
		buf.append("Persistent state ....: ").append(persistent).append('\n');
		buf.append("Version .............: ").append(version).append('\n');
		buf.append("Serial number .......: ").append(serialNumber).append('\n');
		buf.append("Password ............: ").append(password).append('\n');
		buf.append("DNS Server ..........: ").append(dnsServer).append('\n');
		buf.append("Cloud service .......: ").append(cloudService).append('\n');
		buf.append("Cloud service enabled: ").append(cloudServiceEnabled).append('\n');

		return buf.toString();
	}

}
