package li.cryx.sr201.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import li.cryx.sr201.Settings;
import li.cryx.sr201.i18n.XMLResourceBundleControl;
import li.cryx.sr201.util.Closer;
import li.cryx.sr201.util.PropertiesSupport;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 8263665017528562277L;

	private static final Logger LOG = LoggerFactory.getLogger(MainWindow.class);

	private static Settings loadSettings() {
		final File file = new File(System.getProperty("user.home"), "sr201.xml");
		if (file.isFile()) {
			final Properties prop = PropertiesSupport.loadFromXml(file);
			return prop != null ? new Settings(prop) : new Settings();
		} else {
			return new Settings();
		}
	}

	public static void main(final String[] args) {
		new MainWindow();
	}

	/** Translations */
	private final ResourceBundle msg;

	/** User settings */
	private final Settings settings;

	/** Main panel */
	private JPanel panel;

	/** Current detail panel */
	private JPanel currentPanel;

	public MainWindow() {
		// load translation
		msg = ResourceBundle.getBundle("i18n/lang", new XMLResourceBundleControl());
		// load settings
		settings = loadSettings();

		// prepare GUI
		init();
	}

	private void changeMainPanel(final JPanel newPanel) {
		if (currentPanel != null) {
			panel.remove(currentPanel);
		}
		panel.add(newPanel, BorderLayout.CENTER);
		currentPanel = newPanel;

		validate();
		repaint();
	}

	private void init() {
		// set application title
		setTitle(msg.getString("app.title"));

		// handle window closing event manually
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent evt) {
				MainWindow.this.onExit();
			}
		});

		panel = new JPanel(new BorderLayout());
		getContentPane().add(panel);

		showSettingsPanel();

		// ensure a minimal window size
		pack();
		Dimension dim = getSize();
		if (dim.width < 390) {
			dim.width = 390;
		}
		dim.height = (int) (dim.width / 1.618);
		setSize(dim);
		// center window on screen
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void onAcceptSettings() {
		// save settings
		final File file = new File(System.getProperty("user.home"), "sr201.xml");
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			settings.exportProperties().storeToXML(fos, null);
		} catch (final IOException e) {
			LOG.error("Cannot save settings", e);
			new DialogFactory(msg, this).errorTranslate("msg.cannot.save.settings");
		} finally {
			Closer.close(fos);
		}

		// show relay panel
		showTogglePanel();
	}

	private void onExit() {
		// shutdown everyting
		if (currentPanel instanceof TogglePanel) {
			((TogglePanel) currentPanel).close();
		}

		// kill window and AWT thread
		dispose();
	}

	private void showSettingsPanel() {
		final SettingsPanel settingsPanel = new SettingsPanel(msg);
		changeMainPanel(settingsPanel);
		settingsPanel.setFromSettings(settings);

		settingsPanel.getButExit().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				MainWindow.this.onExit();
			}
		});

		settingsPanel.getButAccept().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				settingsPanel.applySettings(settings);
				MainWindow.this.onAcceptSettings();
			}
		});
	}

	private void showTogglePanel() {
		final TogglePanel togglePanel = new TogglePanel(msg);
		changeMainPanel(togglePanel);
		togglePanel.setFromSettings(settings);

		togglePanel.getButExit().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				MainWindow.this.onExit();
			}
		});

		togglePanel.getButSettings().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				togglePanel.close();
				showSettingsPanel();
			}
		});
	}

}
