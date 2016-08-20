package li.cryx.sr201;

import java.io.File;
import java.util.Properties;

import li.cryx.sr201.util.PropertiesSupport;

public class SettingsFactory {

	public static Settings loadSettings() {
		final File file = new File(System.getProperty("user.home"), "sr201.xml");
		if (file.isFile()) {
			final Properties prop = PropertiesSupport.loadFromXml(file);
			return prop != null ? new Settings(prop) : new Settings();
		} else {
			return new Settings();
		}
	}

	private SettingsFactory() {
		// singleton
	}

}
