package org.encog.bot.spider.workload.sql;

/**
 * Status: This class defines the constant status values for both the
 * spider_host and spider_workload tables.
 */
public final class Status {
	/**
	 * The item is waiting to be processed.
	 */
	public static final String STATUS_WAITING = "W";

	/**
	 * The item was processed, but resulted in an error.
	 */
	public static final String STATUS_ERROR = "E";

	/**
	 * The item was processed successfully.
	 */
	public static final String STATUS_DONE = "D";

	/**
	 * The item is currently being processed.
	 */
	public static final String STATUS_PROCESSING = "P";

	/**
	 * This item should be ignored, only applies to hosts.
	 */
	public static final String STATUS_IGNORE = "I";

	/**
	 * Private constructor.
	 */
	private Status() {
	}
}
