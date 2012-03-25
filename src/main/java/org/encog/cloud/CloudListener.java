package org.encog.cloud;

public interface CloudListener {
	void notifyPacket();
	void notifyConnections();
}
