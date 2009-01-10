package org.encog.bot.spider.workload.data;

public enum WorkloadStatus {
	/**
	 * WAITING - Waiting to be processed. 
	 */
	WAITING, 
	
	/**
	 * PROCESSED - Successfully processed. 
	 */
	PROCESSED,
	
	/**
	 * ERROR - Unsuccessfully processed. 
	 */		
	ERROR,
	
	/**
	 * WORKING - Currently being processed.
	 */
	WORKING
}
