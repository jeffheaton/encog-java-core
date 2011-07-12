package org.encog.util.concurrency;

/**
 * Defines that a class is multi-threadable.
 */
public interface MultiThreadable {
	/**
	 * @return The number of threads to use, 0 to automatically 
	 * determine based on core count.
	 */
	int getThreadCount();

	/**
	 * Set the number of threads to use.  
	 * @param numThreads The number of threads to use, or zero to 
	 * automatically determine based on core count.
	 */
	void setThreadCount(int numThreads);
}
