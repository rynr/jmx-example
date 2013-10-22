package de.friendscout24.monitor;

public interface MonitorMBean {

	public String[] getCounters();

	public int getCounter(String name);

}
