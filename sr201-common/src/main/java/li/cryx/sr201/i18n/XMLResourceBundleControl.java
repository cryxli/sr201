package li.cryx.sr201.i18n;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * This class instructs the <code>ResourceBundle</code> factory on how to load
 * translation from XML <code>Properties</code> files.
 *
 * @author cryxli
 */
public class XMLResourceBundleControl extends Control {

	@Override
	public List<String> getFormats(final String baseName) {
		if (baseName == null) {
			throw new NullPointerException();
		}
		// only support XML
		return Arrays.asList("xml");
	}

	@Override
	public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
			final ClassLoader loader, final boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {
		if (baseName == null || locale == null || format == null || loader == null) {
			throw new NullPointerException();
		}
		ResourceBundle bundle = null;
		if ("xml".equals(format)) {
			final String bundleName = toBundleName(baseName, locale);
			final String resourceName = toResourceName(bundleName, format);
			InputStream stream = null;
			if (reload) {
				final URL url = loader.getResource(resourceName);
				if (url != null) {
					final URLConnection connection = url.openConnection();
					if (connection != null) {
						// Disable caches to get fresh data for reloading.
						connection.setUseCaches(false);
						stream = connection.getInputStream();
					}
				}
			} else {
				stream = loader.getResourceAsStream(resourceName);
			}
			if (stream != null) {
				final BufferedInputStream bis = new BufferedInputStream(stream);
				bundle = new XMLResourceBundle(bis);
				bis.close();
			}
		}
		return bundle;
	}

}
