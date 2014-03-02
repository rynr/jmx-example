package de.friendscout24.monitor;

import java.util.Map;

public interface MonitorMBean {

	public Map<String, Integer> getCounters();

	public String[] getCounterNames();

	public int getCounter(String name);

}
