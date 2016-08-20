package li.cryx.sr201.client;

import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JButton;

import li.cryx.sr201.connection.Channel;
import li.cryx.sr201.connection.State;

public class StateButton extends JButton {

	private static final long serialVersionUID = 2915542922568617023L;

	private final ResourceBundle msg;

	private final Channel channel;

	private State state;

	public StateButton(final ResourceBundle msg, final Channel channel) {
		this(msg, channel, State.UNKNOWN);
	}

	public StateButton(final ResourceBundle msg, final Channel channel, final State state) {
		this.msg = msg;
		this.channel = channel;
		setState(state);
	}

	public Channel getChannel() {
		return channel;
	}

	// make square buttons
	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		int s = (int) (d.getWidth() < d.getHeight() ? d.getHeight() : d.getWidth());
		return new Dimension(s, s);
	}

	public State getState() {
		return state;
	}

	public void setState(final State state) {
		this.state = state;

		setIcon(state.icon());

		String tooltip = msg.getString("view.toggle.but.tooltip");
		String channelStr = msg.getString("view.toggle.but.tooltip." + channel.name());
		String stateStr = msg.getString("view.toggle.but.tooltip." + state.name());
		setToolTipText(MessageFormat.format(tooltip, channelStr, stateStr));
	}

}
