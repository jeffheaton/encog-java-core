/*
 * Encog(tm) Core v2.5 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2010 Heaton Research, Inc.
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

package org.encog;

import java.util.HashMap;
import java.util.Map;

import org.encog.engine.EncogEngine;
import org.encog.engine.concurrency.EngineConcurrency;
import org.encog.engine.opencl.EncogCL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Encog class, does little more than provide version information. Also
 * used to hold the ORM session that Encog uses to work with Hibernate.
 * 
 * @author jheaton
 */
public final class Encog {

	/**
	 * The current engog version, this should be read from the properties.
	 */
	public static final String VERSION = "2.6.0";

	/**
	 * The current engog file version, this should be read from the properties.
	 */
	private static final String FILE_VERSION = "1";

	/**
	 * The default precision to use for compares.
	 */
	public static final int DEFAULT_PRECISION = 10;

	/**
	 * Default point at which two doubles are equal.
	 */
	public static final double DEFAULT_DOUBLE_EQUAL = 0.0000001;

	/**
	 * The version of the Encog JAR we are working with. Given in the form
	 * x.x.x.
	 */
	public static final String ENCOG_VERSION = "encog.version";

	/**
	 * The encog file version. This determines of an encog file can be read.
	 * This is simply an integer, that started with zero and is incremented each
	 * time the format of the encog data file changes.
	 */
	public static final String ENCOG_FILE_VERSION = "encog.file.version";

	/**
	 * The instance.
	 */
	private static Encog instance;

	/**
	 * Get the instance to the singleton.
	 * 
	 * @return The instance.
	 */
	public static Encog getInstance() {
		if (Encog.instance == null) {
			Encog.instance = new Encog();
		}
		return Encog.instance;
	}

	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Get the properties as a Map.
	 * 
	 * @return The requested value.
	 */
	private final Map<String, String> properties 
		= new HashMap<String, String>();

	/**
	 * Private constructor.
	 */
	private Encog() {
		this.properties.put(Encog.ENCOG_VERSION, Encog.VERSION);
		this.properties.put(Encog.ENCOG_FILE_VERSION, Encog.FILE_VERSION);
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * Enable OpenCL processing. OpenCL processing allows Encog to use GPU
	 * devices to speed calculations. Not all areas of Encog can use this,
	 * however, GPU's can currently accelerate the training of Feedforward
	 * neural networks.
	 * 
	 * To make use of the GPU you must have OpenCL drivers installed. For more
	 * information on getting OpenCL drivers, visit the following URL.
	 * 
	 * http://www.heatonresearch.com/encog/opencl
	 */
	public void initCL() {
		EncogEngine.getInstance().initCL();
	}

	/**
	 * Provides any shutdown that Encog may need. Currently this shuts down the
	 * thread pool.
	 */
	public void shutdown() {
		EngineConcurrency.getInstance().shutdown(10000);
	}

	/**
	 * @return If Encog is not using GPU/CL processing this attribute will be
	 *         null. Otherwise it holds the Encog CL object.
	 */
	public EncogCL getCL() {
		return EncogEngine.getInstance().getCL();
	}

}
