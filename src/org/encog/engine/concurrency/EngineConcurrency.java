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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.encog.engine.EncogEngineError;


/**
 * This class abstracts thread pools, and potentially grids and other types of
 * concurrency. It is used by other classes inside of Encog to allow tasks to be
 * executed efficiently on multicore machines.
 * 
 * @author jheaton
 * 
 */
public class EngineConcurrency {

	/**
	 * Singleton instance.
	 */
	private static EngineConcurrency instance;
	
	private Throwable threadError;

	private int currentTaskGroup;
	
	/**
	 * @return The instance to the singleton.
	 */
	public static EngineConcurrency getInstance() {
		if (EngineConcurrency.instance == null) {
			EngineConcurrency.instance = new EngineConcurrency();
		}
		return EngineConcurrency.instance;
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
	 * Construct a concurrency object.
	 */
	public EngineConcurrency() {
		this.executor = Executors.newFixedThreadPool(100);
	}

	/**
	 * Process the specified task. It will be processed either now, or queued to
	 * process on the thread pool.
	 * 
	 * @param task
	 *            The task to process.
	 */
	public void processTask(final EngineTask task, final TaskGroup group) {
		if (this.executor == null) {
			task.run();
		} else {
			if( this.threadError!=null ) {
				Throwable t = this.threadError;
				this.threadError = null;
				throw new EncogEngineError(t);
			}
			
			PoolItem item = new PoolItem(task, group);
			if( group!=null )
				group.taskStarting();
			this.executor.execute(item);
		}
	}
	
	public void processTask(final EngineTask task) {
		processTask(task,null);
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
				throw new EncogEngineError(e);
			}
		}
	}
	
    /// <summary>
    /// Create a new task group.
    /// </summary>
    /// <returns>The new task group.</returns>
    public TaskGroup createTaskGroup()
    {
        TaskGroup result = null;
        synchronized (this)
        {
            this.currentTaskGroup++;
            result = new TaskGroup(this.currentTaskGroup);
            
        }
        return result;
    }
    
    public void checkError() throws EncogEngineError
    {
    	if( this.threadError!=null )
    		throw new EncogEngineError(this.threadError);
    }

	public void registerError(Throwable t) {
		this.threadError = t;
		
	}
}
