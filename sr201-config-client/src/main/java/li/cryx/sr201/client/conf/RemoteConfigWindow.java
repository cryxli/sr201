package li.cryx.sr201.client.conf;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import li.cryx.sr201.SettingsFactory;
import li.cryx.sr201.client.DialogFactory;
import li.cryx.sr201.config.BoardState;
import li.cryx.sr201.config.ConfigConnectionBuilder;
import li.cryx.sr201.connection.ConnectionException;
import li.cryx.sr201.connection.IpAddressValidator;
import li.cryx.sr201.i18n.XMLResourceBundleControl;
import li.cryx.sr201.util.IconSupport;
import li.cryx.sr201.util.Icons;

/**
 * This class starts the config tool for the SR-201. It can read and display the
 * current state of a board and attempt to change the settings according to user
 * input.
 *
 * <p>
 * Whenever possible user inputs are validated before they are sent to the
 * board. Mainly IP addresses must comply to the RFC 1918.
 * </p>
 *
 * @author cryxli
 */
public class RemoteConfigWindow extends JFrame {

	private static final long serialVersionUID = 965921233014060445L;

	private static final Logger LOG = LoggerFactory.getLogger(RemoteConfigWindow.class);

	/** Start config tool as standalone application */
	public static void main(final String[] args) {
		new RemoteConfigWindow();
	}

	/** Loaded translations */
	private final ResourceBundle msg;

	/** Current static IP of the board. */
	private String targetIp;

	/** Current settings received from the board. */
	private BoardState config;

	private InfoPanel infoPanel;

	private IpAddressPanel ipPanel;

	private PersistPanel persistPanel;

	private CloudPannel cloudPanel;

	private JButton butSend;

	public RemoteConfigWindow() {
		// load translation
		msg = ResourceBundle.getBundle("i18n/lang", new XMLResourceBundleControl());
		// load settings
		targetIp = SettingsFactory.loadSettings().getIp();

		// prepare GUI
		init();
	}

	private JButton getButSend() {
		if (butSend == null) {
			butSend = new JButton(msg.getString("view.config.send.button"));
			butSend.setIcon(IconSupport.getIcon(Icons.TEST));
		}
		return butSend;
	}

	private void init() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(msg.getString("view.config.caption"));
		new DialogFactory(msg).changeAppTitle("view.config.caption");

		final JPanel main = new JPanel(new FormLayout("f:p:g", "p,4dlu,p,4dlu,p,4dlu,p,4dlu,p,4dlu,p"));
		getContentPane().add(main);
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		final CellConstraints cc = new CellConstraints();

		int row = 1;
		final ConnectionPanel connPanel = new ConnectionPanel(msg);
		main.add(connPanel, cc.xy(1, row));
		connPanel.getTxIp().setText(targetIp);
		connPanel.getButConnect().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				onConnect(connPanel.getTxIp().getText());
			}
		});
		row += 2;
		infoPanel = new InfoPanel(msg);
		main.add(infoPanel, cc.xy(1, row));
		row += 2;
		ipPanel = new IpAddressPanel(msg);
		main.add(ipPanel, cc.xy(1, row));
		row += 2;
		persistPanel = new PersistPanel(msg);
		main.add(persistPanel, cc.xy(1, row));
		row += 2;
		cloudPanel = new CloudPannel(msg);
		main.add(cloudPanel, cc.xy(1, row));
		// option buttons
		row += 2;
		final JPanel butPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		main.add(butPanel, cc.xy(1, row));
		butPanel.add(getButSend());
		getButSend().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				onSend();
			}
		});

		// disable subpanels
		setSubpanelsEnabled(false);

		// center on screen
		pack();
		final Dimension dim = getSize();
		if (dim.width < 400) {
			dim.width = 400;
		}
		setSize(dim);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void onConnect(final String ip) {
		final DialogFactory dialogFactory = new DialogFactory(msg);

		// validate current IP
		if (!new IpAddressValidator().isValid(ip)) {
			dialogFactory.warnTranslate("msg.conf.invalid.ip");
			return;
		}
		targetIp = ip;

		// connect to current IP and read settings
		try {
			setSubpanelsEnabled(false);
			config = new ConfigConnectionBuilder(targetIp).readConfig();
		} catch (final ConnectionException e) {
			LOG.error("Cannot read config", e);
			config = null;
			dialogFactory.error(e.translate(msg));
			return;
		}

		// display current settings
		infoPanel.getTxSerial().setText(config.getSerialNumber());
		infoPanel.getTxVersion().setText(config.getVersion());
		ipPanel.getTxIp().setText(config.getIpAddress());
		ipPanel.getTxSubnet().setText(config.getSubnetMask());
		ipPanel.getTxGateway().setText(config.getGateway());
		persistPanel.getChPersist().setSelected(config.isPersistent());
		cloudPanel.getChService().setSelected(config.isCloudServiceEnabled());
		cloudPanel.getTxService().setText(config.getCloudService());
		cloudPanel.getTxPassword().setText(config.getPassword());
		cloudPanel.getTxDns().setText(config.getDnsServer());

		// enable lower part of window
		setSubpanelsEnabled(true);
	}

	private void onSend() {
		final ConfigConnectionBuilder builder = new ConfigConnectionBuilder(targetIp);
		final DialogFactory dialogFactory = new DialogFactory(msg);
		final IpAddressValidator validIp = new IpAddressValidator();

		if (!config.getIpAddress().equals(ipPanel.getTxIp().getText())) {
			// static IP changed
			if (validIp.isValid(ipPanel.getTxIp().getText())) {
				builder.ip(ipPanel.getTxIp().getText());
			} else {
				// not valid
				dialogFactory.errorTranslate("msg.conf.valid.ip");
				return;
			}
		}
		if (!config.getSubnetMask().equals(ipPanel.getTxSubnet().getText())) {
			// subnet mask changed
			if (validIp.isValid(ipPanel.getTxSubnet().getText())) {
				builder.subnet(ipPanel.getTxSubnet().getText());
			} else {
				// not valid
				dialogFactory.errorTranslate("msg.conf.valid.subnet");
				return;
			}
		}
		if (!config.getGateway().equals(ipPanel.getTxGateway().getText())) {
			// default gateway changed
			if (validIp.isValid(ipPanel.getTxGateway().getText())) {
				builder.gateway(ipPanel.getTxGateway().getText());
			} else {
				// not valid
				dialogFactory.errorTranslate("msg.conf.valid.gateway");
				return;
			}
		}
		if (config.isPersistent() != persistPanel.getChPersist().isSelected()) {
			// persist relay state flag changed
			builder.persistRelayState(persistPanel.getChPersist().isSelected());
		}
		if (config.isCloudServiceEnabled() != cloudPanel.getChService().isSelected()) {
			// enable cloud service flag changed
			builder.enableCloudService(cloudPanel.getChService().isSelected());
		}
		if (!config.getCloudService().equals(cloudPanel.getTxService().getText())) {
			// cloud service host changed
			builder.cloudHost(cloudPanel.getTxService().getText());
		}
		if (!config.getPassword().equals(cloudPanel.getTxPassword().getText())) {
			// cloud service password changed
			builder.password(cloudPanel.getTxPassword().getText());
		}
		if (!config.getDnsServer().equals(cloudPanel.getTxDns().getText())) {
			// DNS server changed
			if (validIp.isValid(cloudPanel.getTxDns().getText())) {
				builder.dns(cloudPanel.getTxDns().getText());
			} else {
				// not valid
				dialogFactory.errorTranslate("msg.conf.valid.dns");
				return;
			}
		}

		if (!builder.hasPendingCommands()) {
			// no changes detected
			dialogFactory.infoTranslate("msg.conf.nothing");
			return;
		}

		// send commands
		try {
			builder.send();
			dialogFactory.infoTranslate("msg.conf.success");
		} catch (final ConnectionException e) {
			LOG.error("Cannot send config", e);
			dialogFactory.error(e.translate(msg));
		}

		// reset GUI, force user to reload config
		config = null;
		setSubpanelsEnabled(false);
	}

	private void setSubpanelsEnabled(final boolean enabled) {
		// enable/disable lower part of window
		infoPanel.setEnabled(enabled);
		ipPanel.setEnabled(enabled);
		persistPanel.setEnabled(enabled);
		cloudPanel.setEnabled(enabled);
		getButSend().setEnabled(enabled);
	}

}
