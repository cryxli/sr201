package li.cryx.sr201.server.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import li.cryx.sr201.connection.Channel;
import li.cryx.sr201.connection.ConnectionException;
import li.cryx.sr201.connection.HighLevelConnection;
import li.cryx.sr201.connection.State;

@RestController
@RequestMapping("/api")
public class Sr201Controller {

	private static final Logger LOG = LoggerFactory.getLogger(Sr201Controller.class);

	private static Map<Channel, State> unknownStates() {
		final Map<Channel, State> states = new HashMap<>();
		for (final Channel c : Channel.values()) {
			if (c != Channel.ALL) {
				states.put(c, State.UNKNOWN);
			}
		}
		return states;
	}

	/** Connection through which to execute commands on the relay board. */
	@Autowired
	private HighLevelConnection conn;

	/**
	 * Send a command to the relay board.
	 *
	 * @param channel
	 *            Channel (relay) indicator. Can be "1" to "8", or "all" to
	 *            switch all relays at once.
	 * @param state
	 *            State indicator. Can be "on" or "1", anything else will be
	 *            interpreted as "off".
	 * @return New states of all relays.
	 */
	@RequestMapping("/channel/{channel}/state/{state}")
	public @ResponseBody Map<String, Object> changeRelay( //
			@PathVariable("channel") final String channel, //
			@PathVariable("state") final String state //
	) {
		// resolve channel
		Channel relay = null;
		if ("all".equalsIgnoreCase(channel) || "x".equalsIgnoreCase(channel)) {
			relay = Channel.ALL;
		} else {
			try {
				relay = Channel.valueOf("CH" + channel);
			} catch (final IllegalArgumentException e) {
				relay = null;
				LOG.warn("Unknown channel: " + channel);
				return nok("Unknown channel");
			}
		}

		// resolve state
		State newState;
		if ("on".equalsIgnoreCase(state) || "1".equals(state)) {
			newState = State.ON;
		} else {
			newState = State.OFF;
		}

		final Map<String, Object> response = new HashMap<>();
		// send command
		Map<Channel, State> states;
		try {
			states = conn.send(relay, newState);
		} catch (final ConnectionException e) {
			states = unknownStates();
			LOG.error("Could not send command", e);
			response.put("nok", "Could not send command");
		}

		response.put("states", statesToMap(states));
		return response;
	}

	/**
	 * Get the current state of the relays.
	 *
	 * @return States of all relays.
	 */
	@RequestMapping("/channel/{channel}")
	public @ResponseBody Map<String, Object> getRelayStates() {
		Map<Channel, State> states;
		try {
			states = conn.getStates();
		} catch (final ConnectionException e) {
			states = unknownStates();
			LOG.error("Could not query relay", e);
		}

		final Map<String, Object> response = new HashMap<>();
		response.put("states", statesToMap(states));
		return response;
	}

	private Map<String, Object> nok(final String msg) {
		final Map<String, Object> response = new HashMap<>();
		response.put("nok", msg);
		return response;
	}

	private Map<String, Object> statesToMap(final Map<Channel, State> stateEnums) {
		final Map<String, Object> states = new HashMap<>();
		for (final Entry<Channel, State> e : stateEnums.entrySet()) {
			int state;
			if (e.getValue() == State.ON) {
				state = 1;
			} else if (e.getValue() == State.OFF) {
				state = 0;
			} else {
				state = 2;
			}
			states.put(String.valueOf(e.getKey().key() - '0'), state);
		}
		return states;
	}

}
