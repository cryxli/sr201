package li.cryx.sr201.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import li.cryx.sr201.Settings;
import li.cryx.sr201.connection.ConnectionException;
import li.cryx.sr201.connection.ConnectionFactory;
import li.cryx.sr201.connection.Sr201Connection;
import li.cryx.sr201.util.Closer;
import li.cryx.sr201.util.IconSupport;
import li.cryx.sr201.util.Icons;

public class SettingsPanel extends JPanel {

	private static final long serialVersionUID = -4290740433164662420L;

	private static final Logger LOG = LoggerFactory.getLogger(SettingsPanel.class);

	private JLabel lbIp;

	private JTextField txIp;

	private JLabel lbPort;

	private JTextField txPort;

	private JLabel lbProtocol;

	private JRadioButton rbTcp;

	private JRadioButton rbUdp;

	private JButton butTest;

	private JButton butAccept;

	private JButton butExit;

	/** Border of valid values. */
	private Border normal;

	/** Border to indicate invalid values. */
	private final Border error = BorderFactory.createLineBorder(Color.RED, 2);

	private final ResourceBundle msg;

	private JButton butSettings;

	public SettingsPanel(final ResourceBundle msg) {
		this.msg = msg;
		init();
	}

	public Settings applySettings(final Settings settings) {
		settings.setIp(getTxIp().getText());
		if (settings.isValid()) {
			getTxIp().setBorder(normal);
		} else {
			getTxIp().setBorder(error);
		}

		settings.setTcp(getRbTcp().isSelected());

		return settings;
	}

	public JButton getButAccept() {
		if (butAccept == null) {
			butAccept = new JButton(msg.getString("view.prop.but.accept.label"));
			butAccept.setIcon(IconSupport.getIcon(Icons.CONNECT));
		}
		return butAccept;
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
			butSettings = new JButton(msg.getString(""));
			butSettings.setIcon(IconSupport.getIcon(Icons.SETTINGS));
		}
		return butSettings;
	}

	public JButton getButTest() {
		if (butTest == null) {
			butTest = new JButton(msg.getString("view.prop.but.test.label"));
			butTest.setIcon(IconSupport.getIcon(Icons.TEST));

			butTest.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent evt) {
					final DialogFactory dialog = new DialogFactory(msg);

					final Settings tempSettings = applySettings(new Settings());
					if (!tempSettings.isValid()) {
						dialog.warnTranslate("msg.conn.factory.ip.invalid");
					} else {
						final Sr201Connection conn = new ConnectionFactory(tempSettings).getConnection();
						try {
							conn.connect();
							dialog.infoTranslate("msg.conn.factory.conn.ok");
						} catch (ConnectionException e) {
							LOG.warn("No connection to relay", e);
							dialog.warnTranslate("msg.conn.factory.cannot.connect");
						} finally {
							Closer.close(conn);
						}
					}
				}
			});
		}
		return butTest;
	}

	private JLabel getLbIp() {
		if (lbIp == null) {
			lbIp = new JLabel(msg.getString("view.prop.ip.label"));
		}
		return lbIp;
	}

	private JLabel getLbPort() {
		if (lbPort == null) {
			lbPort = new JLabel(msg.getString("view.prop.port.label"));
		}
		return lbPort;
	}

	private JLabel getLbProtocol() {
		if (lbProtocol == null) {
			lbProtocol = new JLabel(msg.getString("view.prop.protocol.label"));
		}
		return lbProtocol;
	}

	protected JRadioButton getRbTcp() {
		if (rbTcp == null) {
			rbTcp = new JRadioButton(msg.getString("view.prop.tcp.label"));

			// change port depending on selected protocol
			rbTcp.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(final ChangeEvent evt) {
					if (getRbTcp().isSelected()) {
						getTxPort().setText("6722");
					} else {
						getTxPort().setText("6723");
					}
				}
			});
		}
		return rbTcp;
	}

	protected JRadioButton getRbUcp() {
		if (rbUdp == null) {
			rbUdp = new JRadioButton(msg.getString("view.prop.ucp.label"));
		}
		return rbUdp;
	}

	protected JTextField getTxIp() {
		if (txIp == null) {
			txIp = new JTextField();
			// remember "normal" border
			normal = txIp.getBorder();
		}
		return txIp;
	}

	protected JTextField getTxPort() {
		if (txPort == null) {
			txPort = new JTextField();
			txPort.setEditable(false);
		}
		return txPort;
	}

	private void init() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		final JPanel main = new JPanel(new FormLayout("p,3dlu,f:p:g", "p,4dlu,p,4dlu,p,p"));
		add(main, BorderLayout.NORTH);

		final CellConstraints cc = new CellConstraints();

		int row = 1;
		main.add(getLbIp(), cc.xy(1, row));
		main.add(getTxIp(), cc.xy(3, row));
		row += 2;
		main.add(getLbPort(), cc.xy(1, row));
		main.add(getTxPort(), cc.xy(3, row));
		row += 2;
		main.add(getLbProtocol(), cc.xy(1, row));
		main.add(getRbTcp(), cc.xy(3, row));
		row++;
		main.add(getRbUcp(), cc.xy(3, row));

		final ButtonGroup bg = new ButtonGroup();
		bg.add(getRbTcp());
		bg.add(getRbUcp());

		final JPanel butPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		add(butPanel, BorderLayout.SOUTH);
		butPanel.add(getButTest());
		butPanel.add(getButAccept());
		butPanel.add(getButExit());
	}

	public void setFromSettings(final Settings settings) {
		getTxIp().setText(settings.getIp());
		getRbTcp().setSelected(settings.isTcp());
		getRbUcp().setSelected(!settings.isTcp());
	}

}