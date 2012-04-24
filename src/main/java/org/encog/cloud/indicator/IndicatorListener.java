package org.encog.cloud.indicator;


public interface IndicatorListener {
	void notifyPacket(IndicatorPacket packet);
	void notifyConnections(IndicatorLink link, boolean hasOpened);
}
