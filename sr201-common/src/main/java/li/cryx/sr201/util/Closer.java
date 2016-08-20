package li.cryx.sr201.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * A helper class to silently close connections, streams and alike.
 *
 * @author cryxli
 */
public class Closer {

	/**
	 * Close the given implementation of the <code>Closeable</code> interface.
	 *
	 * @param close
	 *            Connection or stream to close.
	 */
	public static void close(final Closeable close) {
		if (close != null) {
			try {
				close.close();
			} catch (final IOException e) {
				// do nothing
			}
		}
	}

	private Closer() {
		// static singleton
	}

}
