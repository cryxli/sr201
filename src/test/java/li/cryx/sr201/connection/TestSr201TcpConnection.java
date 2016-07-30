package li.cryx.sr201.connection;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;
import li.cryx.sr201.Settings;
import li.cryx.sr201.util.Closer;

public class TestSr201TcpConnection {

	private Sr201Connection conn;

	@Ignore
	@Test
	public void clickThrough() {
		Assert.assertEquals("00000000", conn.send("2X"));
		sleep(250);
		Assert.assertEquals("10000000", conn.send("11"));
		sleep(250);
		Assert.assertEquals("11000000", conn.send("12"));
		sleep(250);
		Assert.assertEquals("01000000", conn.send("21"));
		sleep(250);
		Assert.assertEquals("00000000", conn.send("22"));
	}

	@After
	public void closeConnection() {
		Closer.close(conn);
	}

	@Test
	public void jog() {
		Assert.assertEquals("10000000", conn.send("11*"));
		sleep(1000);
		Assert.assertEquals("00000000", conn.getStates());
	}

	@Before
	public void openConnection() {
		final Properties prop = new Properties();
		prop.setProperty("conn.ip", "192.168.0.201");
		prop.setProperty("conn.protocol", "TCP");

		final Settings settings = new Settings(prop);

		conn = new ConnectionFactory(settings).getConnection();
	}

	@Ignore
	@Test
	public void queryState() {
		Assert.assertEquals("00000000", conn.getStates());
	}

	private void sleep(final long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (final InterruptedException e) {
			// do nothing
		}
	}

	@Ignore
	@Test
	public void timed() {
		Assert.assertEquals("10000000", conn.send("11:2"));
		sleep(2500);
		Assert.assertEquals("00000000", conn.getStates());
	}

}
