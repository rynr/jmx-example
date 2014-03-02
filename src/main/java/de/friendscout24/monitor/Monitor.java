package de.friendscout24.monitor;

import java.lang.management.ManagementFactory;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.AttributeChangeNotification;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor extends NotificationBroadcasterSupport implements
		MonitorMBean {

	private static final String MBEAN_NAME = "de.friendscout24.{0}:type=Monitor";
	private static final Logger LOG = LoggerFactory.getLogger(Monitor.class);
	boolean active;
	Map<String, AtomicInteger> counter;
	private int sequenceNumber;

	public Monitor(String name) {
		counter = new HashMap<String, AtomicInteger>();
		active = true;
		try {
			registerMBean(name);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException | MalformedObjectNameException e) {
			LOG.error("Could not register Monitor-MBean", e);
			active = false;
		}
	}

	private void registerMBean(String name) throws InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException,
			MalformedObjectNameException {
		ManagementFactory.getPlatformMBeanServer().registerMBean(this,
				new ObjectName(MessageFormat.format(MBEAN_NAME, name)));
	}

	public void increaseCounter(String name) {
		if (active) {
			if (counter.containsKey(name)) {
				LOG.debug("Increasing counter " + name);
				sendNotification(name, counter.get(name).incrementAndGet());
			} else {
				LOG.debug("Increasing (new) counter " + name);
				counter.put(name, new AtomicInteger(1));
			}
		} else {
			LOG.info("Did not increment counter " + name + " (inactive)");
		}
	}

	private void sendNotification(String name, int new_counter) {
		sendNotification(new AttributeChangeNotification(this,
				sequenceNumber++, System.currentTimeMillis(), "Counter \""
						+ name + "\" incremented", "Counter", "int",
				new_counter - 1, new_counter));
	}

	@Override
	public Map<String, Integer> getCounters() {
		TreeMap<String, Integer> result = new TreeMap<String, Integer>();
		if (active) {
			for (Entry<String, AtomicInteger> entry : counter.entrySet()) {
				result.put(entry.getKey(), new Integer(entry.getValue().get()));
			}
		}
		return result;
	}

	@Override
	public int getCounter(String name) {
		if (active && counter.containsKey(name)) {
			return counter.get(name).intValue();
		} else {
			return 0;
		}
	}

	@Override
	public String[] getCounterNames() {
		if (active) {
			return counter.keySet().toArray(new String[0]);
		} else {
			return null;
		}
	}
}