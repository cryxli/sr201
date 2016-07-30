package li.cryx.sr201.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import li.cryx.sr201.Settings;
import li.cryx.sr201.connection.Channel;
import li.cryx.sr201.connection.ConnectionException;
import li.cryx.sr201.connection.HighLevelConnection;
import li.cryx.sr201.connection.State;
import li.cryx.sr201.util.IconSupport;
import li.cryx.sr201.util.Icons;

public class TogglePanel extends JPanel {

	private static final long serialVersionUID = -6330786353916074194L;

	private static final Logger LOG = LoggerFactory.getLogger(TogglePanel.class);

	private final ResourceBundle msg;

	private HighLevelConnection conn;

	private final Map<Channel, StateButton> butStates = new HashMap<Channel, StateButton>(8);

	private ActionListener buttonListener;

	private JButton butExit;

	private JButton butSettings;

	public TogglePanel(final ResourceBundle msg) {
		this.msg = msg;
		init();
	}

	public void close() {
		conn.close();
	}

	private StateButton createStateButton(final Channel channel) {
		StateButton but = new StateButton(msg, channel);
		but.addActionListener(buttonListener);
		butStates.put(channel, but);
		return but;
	}

	private void disableButtons() {
		for (JButton but : butStates.values()) {
			but.setEnabled(false);
		}
	}

	private void enableButtons() {
		for (JButton but : butStates.values()) {
			but.setEnabled(true);
		}
	}

	public JButton getButExit() {
		if (butExit == null) {
			butExit = new JButton(msg.getString("view.prop.but.exit.label"));
			butExit.setIcon(IconSupport.getIcon(Icons.EXIT));
		}
		return butExit;
	}

	public JButton getButSettings() {
		if (butSettings == null) {
			butSettings = new JButton(msg.getString("view.toggle.but.settings.label"));
			butSettings.setIcon(IconSupport.getIcon(Icons.SETTINGS));
		}
		return butSettings;
	}

	private void init() {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());

		// create only one listener for all toggle buttons
		buttonListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				final StateButton but = (StateButton) evt.getSource();
				disableButtons();

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final Map<Channel, State> states;
						if (but.getState() == State.ON) {
							states = conn.send(but.getChannel(), State.OFF);
						} else {
							states = conn.send(but.getChannel(), State.ON);
						}
						updateStates(states);
						enableButtons();
					}
				});
			}
		};

		// create panel with 8 toggle buttons
		final JPanel statePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(statePanel, BorderLayout.CENTER);
		statePanel.add(createStateButton(Channel.CH1));
		statePanel.add(createStateButton(Channel.CH2));
		statePanel.add(createStateButton(Channel.CH3));
		statePanel.add(createStateButton(Channel.CH4));
		statePanel.add(createStateButton(Channel.CH5));
		statePanel.add(createStateButton(Channel.CH6));
		statePanel.add(createStateButton(Channel.CH7));
		statePanel.add(createStateButton(Channel.CH8));

		// option buttons
		final JPanel butPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(butPanel, BorderLayout.SOUTH);
		butPanel.add(getButSettings());
		butPanel.add(getButExit());
	}

	public void setFromSettings(final Settings settings) {
		disableButtons();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					conn = new HighLevelConnection(settings);
					final Map<Channel, State> states = conn.getStates();
					updateStates(states);
					enableButtons();
				} catch (ConnectionException e) {
					LOG.error("Cannot connect", e);
					new DialogFactory(msg).error(e.translate(msg));
				}
			}
		});
	}

	private void updateStates(final Map<Channel, State> states) {
		for (Entry<Channel, State> e : states.entrySet()) {
			final StateButton but = butStates.get(e.getKey());
			but.setState(e.getValue());
		}
	}

}
