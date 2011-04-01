package org.encog.app.analyst.util;

import org.encog.app.analyst.AnalystListener;
import org.encog.app.analyst.EncogAnalyst;
import org.encog.engine.StatusReportable;

/**
 * Used to bridge the AnalystListerner to an StatusReportable object.
 *
 */
public class AnalystReportBridge implements StatusReportable {

	private EncogAnalyst analyst;
	
	public AnalystReportBridge(EncogAnalyst analyst) {
		this.analyst = analyst;
	}
	
	@Override
	public void report(int total, int current, String message) {
		for( AnalystListener listener: this.analyst.getListeners() ) {
			listener.report(total,current,message);
		}
		
		
	}

}
