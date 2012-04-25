package org.encog.cloud.indicator;

import org.encog.cloud.indicator.server.IndicatorLink;

public interface IndicatorConnectionListener {
	void notifyConnections(IndicatorLink link, boolean hasOpened);
}
