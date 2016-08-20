package li.cryx.sr201.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that provides common operations on <code>Properties</code> files.
 *
 * @author cryxli
 */
public class PropertiesSupport {

	private static final Logger LOG = LoggerFactory.getLogger(PropertiesSupport.class);

	/**
	 * Load an XML properties file from a file.
	 *
	 * @param file
	 *            Location on disk.
	 * @return The loaded properties file, or, <code>null</code> in case of an
	 *         error.
	 */
	public static Properties loadFromXml(final File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return loadFromXml(fis);
		} catch (IOException e) {
			LOG.error("Failed to load properties: " + file, e);
			return null;
		} finally {
			Closer.close(fis);
		}
	}

	/**
	 * Load an XML properties file from an input stream.
	 *
	 * @param in
	 *            Data as stream.
	 * @return The loaded properties file, or, <code>null</code> in case of an
	 *         error.
	 */
	public static Properties loadFromXml(final InputStream in) {
		try {
			final Properties prop = new Properties();
			prop.loadFromXML(in);
			return prop;
		} catch (IOException e) {
			LOG.error("Failed to load properties from stream", e);
			return null;
		} finally {
			Closer.close(in);
		}
	}

	/**
	 * Load an XML properties file from a classpath resource.
	 *
	 * @param resource
	 *            Location of resource.
	 * @return The loaded properties file, or, <code>null</code> in case of an
	 *         error.
	 */
	public static Properties loadFromXmlResource(final String resource) {
		try {
			final Properties prop = new Properties();
			prop.loadFromXML(PropertiesSupport.class.getResourceAsStream(resource));
			return prop;
		} catch (IOException e) {
			LOG.error("Failed to load properties from resource: " + resource, e);
			return null;
		}
	}

	private PropertiesSupport() {
		// static singleton
	}

}
