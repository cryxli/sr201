package li.cryx.sr201.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class serves as a factory an in-memory cache for images and icons.
 *
 * @author cryxli
 */
public class IconSupport {

	private static final Logger LOG = LoggerFactory.getLogger(IconSupport.class);

	/** Only load images once, then serve them from this cache. */
	private static final Map<String, BufferedImage> cache = new HashMap<String, BufferedImage>();

	/**
	 * Get the icon as defined by the given enum.
	 *
	 * @param icon
	 *            Enum representing the requested icon.
	 * @return Loaded image as icon, or, <code>null</code>.
	 */
	public static ImageIcon getIcon(final Icons icon) {
		return getIcon(icon.resource());
	}

	/**
	 * Get the icon from the indicated resource.
	 *
	 * @param resource
	 *            Classpath to the icon.
	 * @return Loaded image as icon, or, <code>null</code>.
	 */
	public static ImageIcon getIcon(final String resource) {
		Image img = getImage(resource);
		if (img != null) {
			return new ImageIcon(img);
		} else {
			return null;
		}
	}

	/**
	 * Get the image as defined by the given enum.
	 *
	 * @param resource
	 *            Classpath to the image.
	 * @return Loaded image, or, <code>null</code>.
	 */
	public static BufferedImage getImage(final Icons icon) {
		return getImage(icon.resource());
	}

	/**
	 * Get the image as defined by the given enum.
	 *
	 * @param icon
	 *            Enum representing the requested image.
	 * @return Loaded image, or, <code>null</code>.
	 */
	public static BufferedImage getImage(final String resource) {
		if (cache.get(resource) == null) {
			try {
				final BufferedImage img = ImageIO.read(IconSupport.class.getResourceAsStream(resource));
				cache.put(resource, img);
			} catch (IOException e) {
				LOG.warn("Cannot load image from resource: " + resource, e);
			}
		}
		return cache.get(resource);
	}

	private IconSupport() {
		// static singleton
	}

}
