package org.encog.cloud.basic;

public class CloudError extends Error {
	public CloudError(Throwable t) {
		super(t);
	}
	
	public CloudError(String s) {
		super(s);
	}
}
