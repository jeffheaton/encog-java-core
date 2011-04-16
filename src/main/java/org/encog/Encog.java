/*
 * Encog(tm) Core v3.0 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2011 Heaton Research, Inc.
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.encog.plugin.EncogPluginBase;
import org.encog.util.concurrency.EngineConcurrency;

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
	public static final String VERSION = "3.0.0";

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
	
	private Map<Integer,List<EncogPluginBase>> plugins = new HashMap<Integer,List<EncogPluginBase>>();

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
	 * Provides any shutdown that Encog may need. Currently this shuts down the
	 * thread pool.
	 */
	public void shutdown() {
		EngineConcurrency.getInstance().shutdown(10000);
	}

	public void registerPlugin(EncogPluginBase plugin) {
		int type = plugin.getPluginType();
		
		// find or create the list
		List<EncogPluginBase> list;
		if( this.plugins.containsKey(type) ) {
			list = this.plugins.get(type);
		} else {
			list = new ArrayList<EncogPluginBase>();
			this.plugins.put(type, list);
		}
		
		// does this plugin exist already?
		boolean dontAdd = false;
		
		for(EncogPluginBase p: list) {
			if(p.getPluginName().equalsIgnoreCase(plugin.getPluginName())) {
				// did we just find a newer version?
				if( p.getPluginVersion()<plugin.getPluginVersion()) {
					list.remove(p);
				} else {
					dontAdd = true;
				}
			}
		}
		
		// add the plugin
		if( !dontAdd ) {
			list.add(plugin);
		}
	}
	
	public void registerPluginDir(File dir) {
		
	}

}
