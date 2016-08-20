package li.cryx.sr201.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import li.cryx.sr201.Settings;
import li.cryx.sr201.SettingsFactory;
import li.cryx.sr201.connection.HighLevelConnection;

@SpringBootApplication
public class ServerStarter {

	/** Config spring boot to start servlet container on a specific port. */
	@Component
	public class CustomizationBean implements EmbeddedServletContainerCustomizer {

		@Override
		public void customize(final ConfigurableEmbeddedServletContainer container) {
			// apply port
			container.setPort(settings.getServerPort());
		}

	}

	/** Keep user settings in memory */
	private static Settings settings;

	/** Start server */
	public static void main(final String[] args) {
		// load settings
		settings = SettingsFactory.loadSettings();

		// start server
		SpringApplication.run(ServerStarter.class, args);
	}

	/** Define a connection to the relay board. */
	@Bean
	public HighLevelConnection getConnection() {
		// temporarily change the settings to force TCP connection
		final boolean oldValue = settings.isTcp();
		settings.setTcp(true);
		final HighLevelConnection conn = new HighLevelConnection(settings);
		// reset settings
		settings.setTcp(oldValue);
		// return connection
		return conn;
	}

}
