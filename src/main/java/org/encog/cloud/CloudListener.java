package org.encog.cloud;

import org.encog.cloud.basic.CloudPacket;
import org.encog.cloud.basic.CommunicationLink;

public interface CloudListener {
	void notifyPacket(CloudPacket packet);
	void notifyConnections(CommunicationLink link, boolean hasOpened);
}
