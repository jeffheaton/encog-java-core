/*
 * Encog(tm) Core v2.4
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

import java.util.ArrayList;
import java.util.List;

import org.encog.mathutil.IntRange;

/**
 * Used by several Encog training methods to break up a workload. Can also be
 * used to determine the number of threads to use. If zero threads are
 * specified, Encog will query the processor count and determine the best number
 * of threads to use.
 * 
 */
public class DetermineWorkload {

	/**
	 * What is the minimum number of workload entries for a thread to be worthwhile.
	 */
	public static final int MIN_WORTHWHILE = 100;
	
	/**
	 * How many threads to use.
	 */
	private int threadCount;
	
	/**
	 * What is the total workload size?
	 */
	private int workloadSize;

	/**
	 * Determine the workload.
	 * @param threads Threads to use, or zero to allow Encog to pick.
	 * @param workloadSize Total workload size.
	 */
	public DetermineWorkload(int threads, int workloadSize) {

		this.workloadSize = workloadSize;
		if (threads == 0) {
			int num = Runtime.getRuntime().availableProcessors();

			// if there is more than one processor, use processor count +1
			if (num != 1) {
				num++;
			}
			// if there is a single processor, just use one thread

			// Now see how big the training sets are going to be.
			// We want at least 100 training elements in each.
			// This method will likely be further "tuned" in future versions.

			final long recordCount = this.workloadSize;
			final long workPerThread = recordCount / num;

			if (workPerThread < 100) {
				num = Math.max(1, (int) (recordCount / 100));
			}

			this.threadCount = num;
		} else
			this.threadCount = threads;
	}

	/**
	 * Calculate the high and low ranges for each worker.
	 * @return A list of IntRange objects.
	 */
	public List<IntRange> calculateWorkers() {
		final List<IntRange> result = new ArrayList<IntRange>();
		final int sizePerThread = this.workloadSize / this.threadCount;

		// create the workers
		for (int i = 0; i < this.threadCount; i++) {
			final int low = i * sizePerThread;
			int high;

			// if this is the last record, then high to be the last item
			// in the training set.
			if (i == (this.threadCount - 1)) {
				high = this.workloadSize - 1;
			} else {
				high = ((i + 1) * sizePerThread) - 1;
			}

			result.add(new IntRange(high, low));
		}

		return result;
	}

	/**
	 * @return The thread count.
	 */
	public int getThreadCount() {
		return this.threadCount;
	}
}
