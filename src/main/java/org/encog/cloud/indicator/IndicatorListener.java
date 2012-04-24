package org.encog.cloud.indicator;

import org.encog.cloud.basic.CloudPacket;

public interface IndicatorListener {
	void notifyPacket(CloudPacket packet);
	void notifyConnections(IndicatorLink link, boolean hasOpened);
}
