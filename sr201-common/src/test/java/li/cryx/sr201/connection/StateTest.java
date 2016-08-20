package li.cryx.sr201.connection;

import org.junit.Assert;
import org.junit.Test;

/**
 * Ensure additional functionality of enum {@link State}.
 *
 * @author cryxli
 */
public class StateTest {

	@Test
	public void testOffState() {
		final byte rec = '0';
		final byte send = '2';

		Assert.assertEquals(rec, State.OFF.receive());
		Assert.assertEquals(send, State.OFF.send());

		Assert.assertEquals(State.OFF, State.valueOfReceived(rec));
		Assert.assertEquals(State.OFF, State.valueOfSend(send));
	}

	@Test
	public void testOnState() {
		final byte rec = '1';
		final byte send = '1';

		Assert.assertEquals(rec, State.ON.receive());
		Assert.assertEquals(send, State.ON.send());

		Assert.assertEquals(State.ON, State.valueOfReceived(rec));
		Assert.assertEquals(State.ON, State.valueOfSend(send));
	}

	@Test
	public void testUnknownState() {
		final byte rec = '.';
		final byte send = '.';

		Assert.assertEquals(rec, State.UNKNOWN.receive());
		Assert.assertEquals(send, State.UNKNOWN.send());

		Assert.assertEquals(State.UNKNOWN, State.valueOfReceived(rec));
		Assert.assertEquals(State.UNKNOWN, State.valueOfSend(send));
	}

	@Test
	public void testValueOfReceived() {
		final byte b = 'X';
		Assert.assertEquals(State.UNKNOWN, State.valueOfReceived(b));
	}

	@Test
	public void testValueOfSend() {
		final byte b = 'A';
		Assert.assertEquals(State.UNKNOWN, State.valueOfSend(b));
	}

}
