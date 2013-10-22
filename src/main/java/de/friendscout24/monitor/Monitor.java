package de.friendscout24.monitor;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Monitor implements MonitorMBean {

	private static final String MBEAN_NAME = "de.friendscout24.monitor:type=Monitor";
	boolean active;
	Map<String, AtomicInteger> counter;
	private Logger logger;

	private Monitor() {
		logger = LoggerFactory.getLogger(getClass());
		counter = new HashMap<String, AtomicInteger>();
		active = true;
		try {
			registerMBean();
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException
				| MalformedObjectNameException e) {
			logger.error("Could not register Monitor-MBean", e);
			active = false;
		}
	}

	private void registerMBean() throws InstanceAlreadyExistsException, MBeanRegistrationException,
			NotCompliantMBeanException, MalformedObjectNameException {
		ManagementFactory.getPlatformMBeanServer().registerMBean(this, new ObjectName(MBEAN_NAME));
	}

	private static class MonitorHolder {
		public static Monitor INSTANCE = new Monitor();
	}

	public static Monitor getInstance() {
		return MonitorHolder.INSTANCE;
	}

	public void increaseCounter(String name) {
		if (active) {
			if (counter.containsKey(name)) {
				logger.debug("Increasing counter " + name);
				counter.get(name).incrementAndGet();
			} else {
				logger.debug("Increasing (new) counter " + name);
				counter.put(name, new AtomicInteger(1));
			}
		} else {
			logger.info("Did not increment counter " + name + " (inactive)");
		}
	}

	@Override
	public String[] getCounters() {
		if (active) {
			return counter.keySet().toArray(new String[0]);
		} else {
			return null;
		}
	}

	@Override
	public int getCounter(String name) {
		if (active && counter.containsKey(name)) {
			return counter.get(name).intValue();
		} else {
			return 0;
		}
	}

}