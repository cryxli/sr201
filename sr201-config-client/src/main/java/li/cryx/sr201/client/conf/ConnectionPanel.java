package li.cryx.sr201.client.conf;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import li.cryx.sr201.util.IconSupport;
import li.cryx.sr201.util.Icons;

public class ConnectionPanel extends JPanel {

	private static final long serialVersionUID = -8641903408959280465L;

	private final ResourceBundle msg;

	private JLabel lbIp;

	private JTextField txIp;

	private JButton butConnect;

	public ConnectionPanel(final ResourceBundle msg) {
		this.msg = msg;

		setBorder(BorderFactory.createTitledBorder(msg.getString("view.conf.conn.caption")));
		setLayout(new FormLayout("4dlu,p,2dlu,f:p:g,6dlu,p,4dlu,", "p,4dlu"));

		final CellConstraints cc = new CellConstraints();
		final int row = 1;
		add(getLbIp(), cc.xy(2, row));
		add(getTxIp(), cc.xy(4, row));
		add(getButConnect(), cc.xy(6, row));
	}

	public JButton getButConnect() {
		if (butConnect == null) {
			butConnect = new JButton(msg.getString("view.conf.conn.connect.button"));
			butConnect.setIcon(IconSupport.getIcon(Icons.CONNECT));
		}
		return butConnect;
	}

	private JLabel getLbIp() {
		if (lbIp == null) {
			lbIp = new JLabel(msg.getString("view.conf.conn.ip.label"));
		}
		return lbIp;
	}

	public JTextField getTxIp() {
		if (txIp == null) {
			txIp = new JTextField();
		}
		return txIp;
	}

}
