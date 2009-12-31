package org.encog.util;

import org.encog.StatusReportable;

public class StatusCounter implements StatusReportable {

	private int count;
	
	@Override
	public void report(int total, int current, String message) {
		count++;
	}
	
	public int getCount()
	{
		return count;
	}

}
