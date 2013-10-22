package de.friendscout24.monitor;

public class MonitorTest {

	public static void main(String[] args) throws InterruptedException {
		while(true) {
			Thread.sleep(1000);
			Monitor.getInstance().increaseCounter("MonitorTest");
		}
	}

}
