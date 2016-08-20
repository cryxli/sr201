package li.cryx.sr201.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This is a container to keep a loaded language file in memory when it was read
 * from XML.
 *
 * @author cryxli
 */
public class XMLResourceBundle extends ResourceBundle {

	/** List of translated keys */
	private final Properties props;

	/**
	 * Create a new instance and load the translations from the given input
	 * stream.
	 */
	XMLResourceBundle(final InputStream stream) throws IOException {
		props = new Properties();
		props.loadFromXML(stream);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getKeys() {
		return (Enumeration<String>) props.propertyNames();
	}

	@Override
	protected Object handleGetObject(final String key) {
		return props.getProperty(key);
	}

}
