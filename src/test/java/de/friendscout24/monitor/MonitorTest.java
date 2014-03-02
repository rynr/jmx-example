package de.friendscout24.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class MonitorTest {

	private static final int TEST_NAME_LENGTH = 12;
	private static final String TEST_NAME_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private Monitor monitor;

	@Before
	public void setup() {
		monitor = new Monitor(randomString());
	}

	@Test
	public void assertNotExistingCounterCountsZero() {
		monitor.active = true;
		assertThat(monitor.getCounter(randomString()), equalTo(new Integer(0)));
	}

	@Test
	public void assertActiveCounterIncrements() {
		String testKey = randomString();
		monitor.active = true;
		int expected = monitor.getCounter(testKey);
		monitor.increaseCounter(testKey);
		assertThat(monitor.getCounter(testKey), equalTo(new Integer(
				expected + 1)));
	}

	@Test
	public void assertInactiveCounterAlwaysAnswersZero() {
		String testKey = randomString();
		monitor.active = true;
		monitor.increaseCounter(testKey);
		assertTrue(monitor.getCounter(testKey) > 0);
		monitor.active = false;
		assertFalse(monitor.getCounter(testKey) > 0);
	}

	@Test
	public void assertInactiveCounterReturnsEmptyCounterMap() {
		monitor.active = true;
		monitor.increaseCounter(randomString());
		monitor.active = false;
		assertTrue(monitor.getCounters().isEmpty());
	}

	@Test
	public void assertActiveCountersIncludesCounter() {
		String testKey = randomString();
		monitor.active = true;
		monitor.increaseCounter(testKey);
		assertTrue(monitor.getCounters().containsKey(testKey));
	}
	
	@Test
	public void assertNewMonitorHasNoEntries() {
		assertEquals(0, monitor.getCounterNames().length);
	}

	private String randomString() {
		Random random = new Random();
		char[] text = new char[TEST_NAME_LENGTH];
		for (int i = 0; i < TEST_NAME_LENGTH; i++) {
			text[i] = TEST_NAME_CHARACTERS.charAt(random
					.nextInt(TEST_NAME_CHARACTERS.length()));
		}
		return new String(text);
	}
}
