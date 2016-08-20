package li.cryx.sr201.config;

import org.junit.Assert;
import org.junit.Test;

/**
 * Ensure additional functionality of enum {@link Sr201Command}.
 *
 * @author cryxli
 */
public class Sr201CommandTest {

	@Test
	public void Command1() {
		Assert.assertEquals("#11111;", Sr201Command.QUERY_STATE.cmd(1111));
		Assert.assertEquals("#11111;", Sr201Command.QUERY_STATE.cmd(1111, "."));
	}

	@Test
	public void Command2() {
		Assert.assertEquals("#21111,{};", Sr201Command.SET_IP.cmd(1111));
		Assert.assertEquals("#21111,.;", Sr201Command.SET_IP.cmd(1111, "."));
	}

	@Test
	public void Command3() {
		Assert.assertEquals("#31111,{};", Sr201Command.SET_SUBNET.cmd(1111));
		Assert.assertEquals("#31111,.;", Sr201Command.SET_SUBNET.cmd(1111, "."));
	}

	@Test
	public void Command4() {
		Assert.assertEquals("#41111,{};", Sr201Command.SET_GATEWAY.cmd(1111));
		Assert.assertEquals("#41111,.;", Sr201Command.SET_GATEWAY.cmd(1111, "."));
	}

	@Test
	public void Command6off() {
		Assert.assertEquals("#61111,0;", Sr201Command.STATE_TEMPORARY.cmd(1111));
		Assert.assertEquals("#61111,0;", Sr201Command.STATE_TEMPORARY.cmd(1111, "."));
	}

	@Test
	public void Command6on() {
		Assert.assertEquals("#61111,1;", Sr201Command.STATE_PERSISTENT.cmd(1111));
		Assert.assertEquals("#61111,1;", Sr201Command.STATE_PERSISTENT.cmd(1111, "."));
	}

	@Test
	public void Command7() {
		Assert.assertEquals("#71111;", Sr201Command.RESTART.cmd(1111));
		Assert.assertEquals("#71111;", Sr201Command.RESTART.cmd(1111, "."));
	}

	@Test
	public void Command8() {
		Assert.assertEquals("#81111,{};", Sr201Command.SET_DNS.cmd(1111));
		Assert.assertEquals("#81111,.;", Sr201Command.SET_DNS.cmd(1111, "."));
	}

	@Test
	public void Command9() {
		Assert.assertEquals("#91111,{};", Sr201Command.SET_HOST.cmd(1111));
		Assert.assertEquals("#91111,.;", Sr201Command.SET_HOST.cmd(1111, "."));
	}

	@Test
	public void CommandAoff() {
		Assert.assertEquals("#A1111,0;", Sr201Command.CLOUD_DISABLE.cmd(1111));
		Assert.assertEquals("#A1111,0;", Sr201Command.CLOUD_DISABLE.cmd(1111, "."));
	}

	@Test
	public void CommandAon() {
		Assert.assertEquals("#A1111,1;", Sr201Command.CLOUD_ENABLE.cmd(1111));
		Assert.assertEquals("#A1111,1;", Sr201Command.CLOUD_ENABLE.cmd(1111, "."));
	}

}
