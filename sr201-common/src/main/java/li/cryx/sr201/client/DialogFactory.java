package li.cryx.sr201.client;

import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

public class DialogFactory {

	private static String appTitleKey = "app.title";

	private final ResourceBundle resourceBundle;

	private final Component parent;

	public DialogFactory(final ResourceBundle resourceBundle) {
		this(resourceBundle, null);
	}

	public DialogFactory(final ResourceBundle resourceBundle, final Component parent) {
		this.resourceBundle = resourceBundle;
		this.parent = parent;
	}

	public void changeAppTitle(final String langKey) {
		appTitleKey = langKey;
	}

	public void error(final String msg) {
		JOptionPane.showMessageDialog(parent, msg, resourceBundle.getString(appTitleKey), JOptionPane.ERROR_MESSAGE);
	}

	public void errorTranslate(final String msgKey) {
		error(resourceBundle.getString(msgKey));
	}

	public void info(final String msg) {
		JOptionPane.showMessageDialog(parent, msg, resourceBundle.getString(appTitleKey),
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void infoTranslate(final String msgKey) {
		info(resourceBundle.getString(msgKey));
	}

	public void warn(final String msg) {
		JOptionPane.showMessageDialog(parent, msg, resourceBundle.getString(appTitleKey), JOptionPane.WARNING_MESSAGE);
	}

	public void warnTranslate(final String msgKey) {
		warn(resourceBundle.getString(msgKey));
	}

}
