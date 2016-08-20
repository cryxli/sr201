package li.cryx.sr201.client.conf;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class CloudPannel extends JPanel {

	private static final long serialVersionUID = 4046052815511264897L;

	private final ResourceBundle msg;

	private JCheckBox chService;

	private JLabel lbService;

	private JTextField txService;

	private JLabel lbPassword;

	private JTextField txPassword;

	private JLabel lbDns;

	private JTextField txDns;

	public CloudPannel(final ResourceBundle msg) {
		this.msg = msg;

		setBorder(BorderFactory.createTitledBorder(msg.getString("view.conf.cloud.caption")));
		setLayout(new FormLayout("4dlu,p,2dlu,f:p:g,4dlu,", "p,4dlu,p,4dlu,p,4dlu,p,4dlu"));
		final CellConstraints cc = new CellConstraints();

		int row = 1;
		add(getChService(), cc.xyw(2, row, 3));
		row += 2;
		add(getLbService(), cc.xy(2, row));
		add(getTxService(), cc.xy(4, row));
		row += 2;
		add(getLbPassword(), cc.xy(2, row));
		add(getTxPassword(), cc.xy(4, row));
		row += 2;
		add(getLbDns(), cc.xy(2, row));
		add(getTxDns(), cc.xy(4, row));
	}

	public JCheckBox getChService() {
		if (chService == null) {
			chService = new JCheckBox(msg.getString("view.conf.cloud.enabled.label"));
		}
		return chService;
	}

	private JLabel getLbDns() {
		if (lbDns == null) {
			lbDns = new JLabel(msg.getString("view.conf.cloud.dns.label"));
		}
		return lbDns;
	}

	private JLabel getLbPassword() {
		if (lbPassword == null) {
			lbPassword = new JLabel(msg.getString("view.conf.cloud.password.label"));
		}
		return lbPassword;
	}

	private JLabel getLbService() {
		if (lbService == null) {
			lbService = new JLabel(msg.getString("view.conf.cloud.service.label"));
		}
		return lbService;
	}

	public JTextField getTxDns() {
		if (txDns == null) {
			txDns = new JTextField();
		}
		return txDns;
	}

	public JTextField getTxPassword() {
		if (txPassword == null) {
			txPassword = new JTextField();
		}
		return txPassword;
	}

	public JTextField getTxService() {
		if (txService == null) {
			txService = new JTextField();
		}
		return txService;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		getChService().setEnabled(enabled);
		getLbService().setEnabled(enabled);
		getTxService().setEnabled(enabled);
		getLbPassword().setEnabled(enabled);
		getTxPassword().setEnabled(enabled);
		getLbDns().setEnabled(enabled);
		getTxDns().setEnabled(enabled);
	}

}
