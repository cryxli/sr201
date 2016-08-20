package li.cryx.sr201.connection;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Custom exception class that can be thrown in the context of a
 * {@link Sr201Connection}.
 *
 * <p>
 * The {@link #getMessage()} method will return a language not the error message
 * itself. You can use the {@link #translate(ResourceBundle)} method to turn it
 * into an error text by supplying an appropriate <code>ResourceBundle</code>.
 * </p>
 *
 * @author cryxli
 */
public class ConnectionException extends RuntimeException {

	private static final long serialVersionUID = 7616505850922142113L;

	/** Optional arguments for the translated message */
	private Object[] arguments = null;

	/**
	 * Create a new exception.
	 *
	 * @param langKey
	 *            Language key into the translations.
	 */
	public ConnectionException(final String langKey) {
		super(langKey);
	}

	/**
	 * Create a new exception.
	 *
	 * @param langKey
	 *            Language key into the translations.
	 * @param arguments
	 *            Optional arguments for the translated message.
	 */
	public ConnectionException(final String langKey, final Object... arguments) {
		super(langKey);
		this.arguments = arguments;
	}

	/**
	 * Create a new exception.
	 *
	 * @param langKey
	 *            Language key into the translations.
	 * @param cause
	 *            Child exception causing this one.
	 */
	public ConnectionException(final String langKey, final Throwable cause) {
		super(langKey, cause);
	}

	/**
	 * Create a new exception.
	 *
	 * @param langKey
	 *            Language key into the translations.
	 * @param cause
	 *            Child exception causing this one.
	 * @param arguments
	 *            Optional arguments for the translated message.
	 */
	public ConnectionException(final String langKey, final Throwable cause, final Object... arguments) {
		super(langKey, cause);
		this.arguments = arguments;
	}

	/**
	 * Get the translated error message.
	 *
	 * @param msg
	 *            ResourceBundle capable of translating the language keys.
	 * @return Translated message, arguments are already replaced.
	 * @see MessageFormat
	 */
	public String translate(final ResourceBundle msg) {
		if (arguments != null) {
			return MessageFormat.format(msg.getString(getMessage()), arguments);
		} else {
			return msg.getString(getMessage());
		}
	}

}
