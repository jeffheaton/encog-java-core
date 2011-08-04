package org.encog.cloud.node;

public class CloudSignal {
	private final String clientName;
	private final String signalName;
		
	public CloudSignal(String clientName, String signalName) {
		super();
		this.clientName = clientName;
		this.signalName = signalName;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public String getSignalName() {
		return signalName;
	}
	
	
}
