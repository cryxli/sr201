package li.cryx.sr201.client.conf;

import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class PersistPanel extends JPanel {

	private static final long serialVersionUID = 2090270720647409113L;

	private final ResourceBundle msg;

	private JCheckBox chPersist;

	public PersistPanel(final ResourceBundle msg) {
		this.msg = msg;

		setBorder(BorderFactory.createTitledBorder(msg.getString("view.conf.persist.caption")));
		setLayout(new FormLayout("4dlu,p,4dlu,", "p,4dlu"));
		final CellConstraints cc = new CellConstraints();

		final int row = 1;
		add(getChPersist(), cc.xy(2, row));
	}

	public JCheckBox getChPersist() {
		if (chPersist == null) {
			chPersist = new JCheckBox(msg.getString("view.conf.persist.enable.label"));
		}
		return chPersist;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);

		chPersist.setEnabled(enabled);
	}

}
