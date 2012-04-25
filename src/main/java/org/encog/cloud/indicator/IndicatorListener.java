package org.encog.cloud.indicator;

import org.encog.cloud.indicator.server.IndicatorLink;
import org.encog.cloud.indicator.server.IndicatorPacket;


public interface IndicatorListener {
	void notifyConnect(IndicatorLink theLink);
	void notifyPacket(IndicatorPacket packet);	
	void notifyTermination();
}
