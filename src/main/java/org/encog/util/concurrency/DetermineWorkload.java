/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.concurrency;

import java.util.ArrayList;
import java.util.List;

import org.encog.EncogError;
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
	 * What is the minimum number of workload entries for a thread to be
	 * worthwhile.
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
	 * 
	 * @param threads
	 *            Threads to use, or zero to allow Encog to pick.
	 * @param workloadSize
	 *            Total workload size.
	 */
	public DetermineWorkload(final int threads, final int workloadSize) {

		if( workloadSize==0) {
			throw new EncogError("Workload is of size zero.");
		}
		
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
		} else {
			this.threadCount = Math.min(threads, workloadSize);
		}
	}

	/**
	 * Calculate the high and low ranges for each worker.
	 * 
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
