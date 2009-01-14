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
