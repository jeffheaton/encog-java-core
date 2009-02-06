/*
 * Encog Artificial Intelligence Framework v1.x
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

public class EncogConcurrency {

	private int maxThreads;
	private static EncogConcurrency instance;
	private ExecutorService executor;

	public EncogConcurrency() {
		setMaxThreads(0);
	}

	public static EncogConcurrency getInstance() {
		if (instance == null)
			instance = new EncogConcurrency();
		return instance;
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
		if (this.maxThreads > 0) {
			executor = Executors.newFixedThreadPool(maxThreads);
		} else {
			executor = null;
		}
	}

	public void processTask(EncogTask task) {
		if (executor == null)
			task.run();
		else
			executor.execute(task);
	}

	public void waitForComplete(long timeout) {
		if (executor != null) {
			try {
				executor.awaitTermination(timeout, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				throw new EncogError(e);
			}
		}

	}
}
