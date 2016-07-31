package li.cryx.sr201.client.conf;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class InfoPanel extends JPanel {

	private static final long serialVersionUID = -3120902001715562919L;

	private final ResourceBundle msg;

	private JLabel lbSerial;

	private JTextField txSerial;

	private JLabel lbVersion;

	private JTextField txVersion;

	public InfoPanel(final ResourceBundle msg) {
		this.msg = msg;

		setBorder(BorderFactory.createTitledBorder(msg.getString("view.conf.info.caption")));
		setLayout(new FormLayout("4dlu,p,2dlu,f:p:g,4dlu,", "p,4dlu,p,4dlu"));
		final CellConstraints cc = new CellConstraints();

		int row = 1;
		add(getLbSerial(), cc.xy(2, row));
		add(getTxSerial(), cc.xy(4, row));
		row += 2;
		add(getLbVersion(), cc.xy(2, row));
		add(getTxVersion(), cc.xy(4, row));
	}

	private JLabel getLbSerial() {
		if (lbSerial == null) {
			lbSerial = new JLabel(msg.getString("view.conf.info.serial.label"));
		}
		return lbSerial;
	}

	private JLabel getLbVersion() {
		if (lbVersion == null) {
			lbVersion = new JLabel(msg.getString("view.conf.info.version.label"));
		}
		return lbVersion;
	}

	public JTextField getTxSerial() {
		if (txSerial == null) {
			txSerial = new JTextField();
			txSerial.setEditable(false);
		}
		return txSerial;
	}

	public JTextField getTxVersion() {
		if (txVersion == null) {
			txVersion = new JTextField();
			txVersion.setEditable(false);
		}
		return txVersion;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		getLbSerial().setEnabled(enabled);
		getLbVersion().setEnabled(enabled);
	}

}
