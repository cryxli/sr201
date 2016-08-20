package li.cryx.sr201.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Ensure that {@link BoardState} does parse the config of the board correctly.
 *
 * @author cryxli
 */
public class BoardStateTest {

	@Test
	public void testParseState() {
		BoardState parsed = BoardState.parseState(">1,2,3,4,5,6,77777777777777//////,8,9,10;");
		Assert.assertEquals("1", parsed.getIpAddress());
		Assert.assertEquals("2", parsed.getSubnetMask());
		Assert.assertEquals("3", parsed.getGateway());
		Assert.assertFalse(parsed.isPersistent());
		Assert.assertEquals("1.0.6", parsed.getVersion());
		Assert.assertEquals("77777777777777", parsed.getSerialNumber());
		Assert.assertEquals("//////", parsed.getPassword());
		Assert.assertEquals("8", parsed.getDnsServer());
		Assert.assertEquals("9", parsed.getCloudService());
		Assert.assertFalse(parsed.isCloudServiceEnabled());

		parsed = BoardState.parseState(">a,a,a,a,1,a,aaaaaaaaaaaaaabbbbbb,a,a,a;");
		Assert.assertTrue(parsed.isPersistent());
		Assert.assertFalse(parsed.isCloudServiceEnabled());

		parsed = BoardState.parseState(">a,a,a,a,a,a,aaaaaaaaaaaaaabbbbbb,a,a,1;");
		Assert.assertFalse(parsed.isPersistent());
		Assert.assertTrue(parsed.isCloudServiceEnabled());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseStateEndsWith() {
		BoardState.parseState(">foobar");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseStateNotNull() {
		BoardState.parseState(null);
	}

	@Test(expected = StringIndexOutOfBoundsException.class)
	public void testParseStateSerialNumberTooShort() {
		BoardState.parseState(">1,2,3,4,5,6,7,8,9,10;");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseStateStartsWith() {
		BoardState.parseState("foobar;");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseStateTooShort() {
		BoardState.parseState(">;");
	}

}
