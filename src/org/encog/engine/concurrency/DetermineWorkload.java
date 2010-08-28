/*
 * Encog(tm) Core v2.5 
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

package org.encog.engine.concurrency;

import java.util.ArrayList;
import java.util.List;

import org.encog.engine.EncogEngineError;
import org.encog.engine.util.IntRange;

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
	private int cpuWorkerCount;

	/**
	 * What is the total workload size?
	 */
	private final int totalWorkloadSize;

	/**
	 * CL worker count.
	 */
	private int clWorkerCount;

	/**
	 * The total task count.
	 */
	private int totalWorkerCount;

	/**
	 * Workloads for CPU workers.
	 */
	private final List<IntRange> cpuRanges = new ArrayList<IntRange>();

	/**
	 * Workloads for OpenCL workers.
	 */
	private List<IntRange> clRanges = new ArrayList<IntRange>();
	
	/**
	 * Determine the workload.
	 * @param threads Threads to use, or zero to allow Encog to
	 * pick.
	 * @param workloadSize Total workload size.
	 */
	public DetermineWorkload(final int threads, final int workloadSize) {
		this(threads, 0, workloadSize);
	}

	/**
	 * Determine the workload, consider CL count. If worker count is zero,
	 * Encog picks using
	 * processor count. If worker count is -1 then no CPU threads will be
	 * used.
	 * @param cpuWorkerCount Threads to use, or zero to allow Encog to
	 * pick.
	 * @param clWorkerCount The number of CL workers.
	 * @param workloadSize Total workload size.
	 */
	public DetermineWorkload(int cpuWorkerCount, final int clWorkerCount,
			final int workloadSize) {

		this.cpuWorkerCount = cpuWorkerCount;
		this.clWorkerCount = clWorkerCount;
		this.totalWorkerCount = clWorkerCount + cpuWorkerCount;
		this.totalWorkloadSize = workloadSize;

		if (cpuWorkerCount == 0) {
			int num = Runtime.getRuntime().availableProcessors();

			// if there is more than one processor, use processor count +1
			if (num != 1) {
				num++;
			}
			// if there is a single processor, just use one thread

			// Now see how big the training sets are going to be.
			// We want at least 100 training elements in each.
			// This method will likely be further "tuned" in future versions.

			final long recordCount = this.totalWorkloadSize;
			final long workPerThread = recordCount / num;

			if (workPerThread < DetermineWorkload.MIN_WORTHWHILE) {
				// if we need to reduce, then cut the CL workers to zero
				num = Math.max(1,
						(int) (recordCount / DetermineWorkload.MIN_WORTHWHILE));
				this.clWorkerCount = 0;
			}

			this.cpuWorkerCount = num;
			this.totalWorkerCount = this.clWorkerCount + this.cpuWorkerCount;
		} else {
			if (cpuWorkerCount == -1) {
				cpuWorkerCount = 0;
			}

			this.totalWorkerCount = clWorkerCount + cpuWorkerCount;
			if (this.totalWorkerCount > workloadSize) {
				this.clWorkerCount = 0;
			}
			this.totalWorkerCount = clWorkerCount + cpuWorkerCount;
			this.totalWorkerCount = Math.min(this.totalWorkerCount,
					workloadSize);
		}
	}

	/**
	 * Calculate the high and low ranges for each worker.
	 */
	public void calculateWorkers() {
		this.cpuRanges.clear();
		this.clRanges.clear();

		if (this.totalWorkerCount == 0) {
			throw new EncogEngineError("Can't train with zero workers.");
		}

		final int baseSizePerThread = this.totalWorkloadSize
				/ this.totalWorkerCount;
		final int clSizePerThread = (int)baseSizePerThread;
		final int cpuWorkloadSize = Math.max(this.totalWorkloadSize
				- (clSizePerThread * this.clWorkerCount), 0);
		final int cpuSizePerThread = Math.max(cpuWorkloadSize
				/ this.cpuWorkerCount, 0);

		int index = 0;

		// create the CL workers
		for (int i = 0; i < this.clWorkerCount; i++) {
			final int low = index;
			final int high = (low + clSizePerThread) - 1;
			this.clRanges.add(new IntRange(high, low));
			index += clSizePerThread;
		}

		// create the CPU workers
		for (int i = 0; i < this.cpuWorkerCount; i++) {
			final int low = index;
			int high;

			// if this is the last record, then high to be the last item
			// in the training set.
			if (i == (this.cpuWorkerCount - 1)) {
				high = this.totalWorkloadSize - 1;
			} else {
				high = (low + cpuSizePerThread) - 1;
			}

			this.cpuRanges.add(new IntRange(high, low));
			index += cpuSizePerThread;
		}

	}

	/**
	 * @return the clRanges
	 */
	public List<IntRange> getClRanges() {
		return this.clRanges;
	}

	/**
	 * @return Workload ranges for CL workers.
	 */
	public List<IntRange> getCLRanges() {
		return this.clRanges;
	}

	/**
	 * @return The CL thread count.
	 */
	public int getCLWorkerCount() {
		return this.clWorkerCount;
	}

	/**
	 * @return Workload ranges for CPU workers.
	 */
	public List<IntRange> getCPURanges() {
		return this.cpuRanges;
	}

	/**
	 * @return The thread count.
	 */
	public int getCPUWorkerCount() {
		return this.cpuWorkerCount;
	}

	/**
	 * @return The thread count.
	 */
	public int getTotalWorkerCount() {
		return this.totalWorkerCount;

	}

	/**
	 * @return What is the total workload size?
	 */
	public int getTotalWorkloadSize() {
		return this.totalWorkloadSize;
	}

	/**
	 * @param clRanges
	 *            the clRanges to set
	 */
	public void setClRanges(final List<IntRange> clRanges) {
		this.clRanges = clRanges;
	}
}
