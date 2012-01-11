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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.EncogError;

/**
 * This class abstracts thread pools, and potentially grids and other types of
 * concurrency. It is used by other classes inside of Encog to allow tasks to be
 * executed efficiently on multicore machines.
 * 
 * @author jheaton
 * 
 */
public class EngineConcurrency implements MultiThreadable {

	/**
	 * Singleton instance.
	 */
	private static EngineConcurrency instance = new EngineConcurrency();

	/**
	 * @return The instance to the singleton.
	 */
	public static EngineConcurrency getInstance() {		
		return EngineConcurrency.instance;
	}

	/**
	 * An error that was caught in one of the threads. Will be thrown by the
	 * main thread.
	 */
	private Throwable threadError;
	
	/**
	 * The thread count.
	 */
	private int threadCount;

	/**
	 * The current task group, used to ensure that all threads finish at the
	 * same time.
	 */
	private int currentTaskGroup;

	/**
	 * The executor service we are using.
	 */
	private ExecutorService executor;

	/**
	 * Construct a concurrency object.
	 */
	public EngineConcurrency() {
		Runtime runtime = Runtime.getRuntime();        
        int threads = runtime.availableProcessors();
        if( threads>1 )
        	threads++;
		this.executor = Executors.newFixedThreadPool(threads);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setThreadCount(int t) {
		this.executor.shutdown();
		int threads = t;
		
		if( threads==0 )
		{
			Runtime runtime = Runtime.getRuntime();
			threads = runtime.availableProcessors();
	        if( threads>1 )
	        	threads++;	
		}		
        
		this.executor = Executors.newFixedThreadPool(threads);
		this.threadCount = threads;
	}

	/**
	 * Check to see if one of the threads has thrown an error. If so, then throw
	 * that error.
	 */
	public void checkError() {
		if (this.threadError != null) {
			throw new EncogError(this.threadError);
		}
	}
	
	/**
	 * Create a new task group.
	 * @return The new task group.
	 */
	public TaskGroup createTaskGroup() {
		TaskGroup result = null;
		synchronized (this) {
			this.currentTaskGroup++;
			result = new TaskGroup(this.currentTaskGroup);

		}
		return result;
	}

	/**
	 * Process the specified task.
	 * @param task The task to process.
	 */
	public void processTask(final EngineTask task) {
		processTask(task, null);
	}

	/**
	 * Process the specified task. It will be processed either now, or queued to
	 * process on the thread pool.
	 * 
	 * @param task
	 *            The task to process.
	 * @param group The task group.
	 */
	public void processTask(final EngineTask task, final TaskGroup group) {
		if (this.executor == null) {
			task.run();
		} else {
			if (this.threadError != null) {
				final Throwable t = this.threadError;
				this.threadError = null;
				throw new EncogError(t);
			}

			final PoolItem item = new PoolItem(task, group);
			if (group != null) {
				group.taskStarting();
			}
			this.executor.execute(item);
		}
	}

	/**
	 * Allows threads to register errors, these errors will be thrown by the
	 * main thread.
	 * 
	 * @param t
	 *            The error to register.
	 */
	public void registerError(final Throwable t) {
		synchronized (this) {
			this.threadError = t;
		}

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
				throw new EncogError(e);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getThreadCount() {
		return this.threadCount;
	}
}
