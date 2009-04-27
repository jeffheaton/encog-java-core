/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
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

	private static EncogConcurrency instance;

	public static EncogConcurrency getInstance() {
		if (EncogConcurrency.instance == null) {
			EncogConcurrency.instance = new EncogConcurrency();
		}
		return EncogConcurrency.instance;
	}

	private int maxThreads;

	private ExecutorService executor;

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public EncogConcurrency() {
		setMaxThreads(0);
	}

	public void processTask(final EncogTask task) {
		if (this.executor == null) {
			task.run();
		} else {
			this.executor.execute(task);
		}
	}

	public void setMaxThreads(final int maxThreads) {
		this.maxThreads = maxThreads;
		if (this.maxThreads > 0) {
			this.executor = Executors.newFixedThreadPool(maxThreads);
		} else {
			this.executor = null;
		}
	}

	public void waitForComplete(final long timeout) {
		if (this.executor != null) {
			try {
				this.executor.awaitTermination(timeout, TimeUnit.SECONDS);
			} catch (final InterruptedException e) {
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Exception", e);
				}
				throw new EncogError(e);
			}
		}

	}
}
