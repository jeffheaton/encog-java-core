package org.encog.util.concurrency;

import java.util.ArrayList;
import java.util.List;

import org.encog.neural.data.Indexable;
import org.encog.neural.networks.training.propagation.gradient.GradientWorker;
import org.encog.mathutil.IntRange;

public class DetermineWorkload {

	private int threadCount;
	private int workloadSize;

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
	
	public List<IntRange> calculateWorkers()
	{
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

			result.add( new IntRange(high,low));
		}
		
		return result;
	}

	public int getThreadCount() {
		return this.threadCount;
	}
}
