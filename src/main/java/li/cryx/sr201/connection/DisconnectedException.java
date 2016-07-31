package li.cryx.sr201.connection;

public class DisconnectedException extends ConnectionException {

	private static final long serialVersionUID = -6902261289003066861L;

	public DisconnectedException() {
		super("msg.tcp.disconnected");
	}

}
