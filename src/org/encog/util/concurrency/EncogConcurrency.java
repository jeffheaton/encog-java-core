/*
 * Encog(tm) Artificial Intelligence Framework v2.3
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.util.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.EncogError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class abstracts thread pools, and potentially grids and other types of
 * concurrency. It is used by other classes inside of Encog to allow tasks to be
 * executed efficiently on multicore machines.
 * 
 * @author jheaton
 * 
 */
public class EncogConcurrency {

	/**
	 * Singleton instance.
	 */
	private static EncogConcurrency instance;

	/**
	 * @return The instance to the singleton.
	 */
	public static EncogConcurrency getInstance() {
		if (EncogConcurrency.instance == null) {
			EncogConcurrency.instance = new EncogConcurrency();
		}
		return EncogConcurrency.instance;
	}

	/**
	 * Maximum number of threads.
	 */
	private int maxThreads;

	/**
	 * The executor service we are using.
	 */
	private ExecutorService executor;

	/**
	 * The logging object.
	 */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Construct a concurrency object.
	 */
	public EncogConcurrency() {
		setMaxThreadsToCoreCount();
	}

	/**
	 * Process the specified task. It will be processed either now, or queued to
	 * process on the thread pool.
	 * 
	 * @param task
	 *            The task to process.
	 */
	public void processTask(final EncogTask task) {
		if (this.executor == null) {
			task.run();
		} else {
			this.executor.execute(task);
		}
	}

	/**
	 * Set the maximum number of threads to use.
	 * 
	 * @param maxThreads
	 *            Maximum number of threads to use.
	 */
	public void setMaxThreads(final int maxThreads) {
		this.maxThreads = maxThreads;
		if (this.maxThreads > 0) {
			this.executor = Executors.newFixedThreadPool(maxThreads);
		} else {
			this.executor = null;
		}
	}

	/**
	 * Set the max threads to the number of processors.
	 */
	public void setMaxThreadsToCoreCount() {
		setMaxThreads(Runtime.getRuntime().availableProcessors());
	}

	/**
	 * Wait for all threads in the pool to complete.
	 * 
	 * @param timeout
	 *            How long to wait for all threads to complete.
	 */
	public void shutdown(final long timeout) {
		if (this.executor != null) {
			try {
				this.executor.shutdown();
				this.executor.awaitTermination(timeout, TimeUnit.SECONDS);
				this.executor = null;
			} catch (final InterruptedException e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Exception", e);
				}
				throw new EncogError(e);
			}
		}

	}
}
