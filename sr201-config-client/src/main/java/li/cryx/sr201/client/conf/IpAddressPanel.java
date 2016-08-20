package li.cryx.sr201.client.conf;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class IpAddressPanel extends JPanel {

	private static final long serialVersionUID = -280529740503393758L;

	private final ResourceBundle msg;

	private JLabel lbIp;

	private JTextField txIp;

	private JLabel lbSubnet;

	private JTextField txSubnet;

	private JLabel lbGateway;

	private JTextField txGateway;

	public IpAddressPanel(final ResourceBundle msg) {
		this.msg = msg;

		setBorder(BorderFactory.createTitledBorder(msg.getString("view.conf.ip.caption")));
		setLayout(new FormLayout("4dlu,p,2dlu,f:p:g,4dlu,", "p,4dlu,p,4dlu,p,4dlu"));
		final CellConstraints cc = new CellConstraints();

		int row = 1;
		add(getLbIp(), cc.xy(2, row));
		add(getTxIp(), cc.xy(4, row));
		row += 2;
		add(getLbSubnet(), cc.xy(2, row));
		add(getTxSubnet(), cc.xy(4, row));
		row += 2;
		add(getLbGateway(), cc.xy(2, row));
		add(getTxGateway(), cc.xy(4, row));
	}

	private JLabel getLbGateway() {
		if (lbGateway == null) {
			lbGateway = new JLabel(msg.getString("view.conf.ip.gateway.label"));
		}
		return lbGateway;
	}

	private JLabel getLbIp() {
		if (lbIp == null) {
			lbIp = new JLabel(msg.getString("view.conf.ip.ip.label"));
		}
		return lbIp;
	}

	private JLabel getLbSubnet() {
		if (lbSubnet == null) {
			lbSubnet = new JLabel(msg.getString("view.conf.ip.subnet.label"));
		}
		return lbSubnet;
	}

	public JTextField getTxGateway() {
		if (txGateway == null) {
			txGateway = new JTextField();
		}
		return txGateway;
	}

	public JTextField getTxIp() {
		if (txIp == null) {
			txIp = new JTextField();
		}
		return txIp;
	}

	public JTextField getTxSubnet() {
		if (txSubnet == null) {
			txSubnet = new JTextField();
		}
		return txSubnet;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		getLbIp().setEnabled(enabled);
		getTxIp().setEnabled(enabled);
		getLbSubnet().setEnabled(enabled);
		getTxSubnet().setEnabled(enabled);
		getLbGateway().setEnabled(enabled);
		getTxGateway().setEnabled(enabled);
	}

}
