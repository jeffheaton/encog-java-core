/*
 * Encog Neural Network and Bot Library for Java v1.x
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008, Heaton Research Inc., and individual contributors.
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

package org.encog;

import java.util.HashMap;
import java.util.Map;

import org.encog.util.orm.ORMSession;


/**
 * Main Encog class, does little more than provide version information.
 * 
 * @author jheaton
 */
public final class Encog {

	/**
	 * The version of the Encog JAR we are working with. Given in the form
	 * x.x.x.
	 */
	public static final String ENCOG_VERSION = "encog.version";

	/**
	 * The encog file version. This determines of an encog file can be read.
	 * This is simply an integer, that started with zero and is incramented each
	 * time the format of the encog data file changes.
	 */
	public static final String ENCOG_FILE_VERSION = "encog.file.version";

	/**
	 * The instance.
	 */
	private static Encog instance;
	
	private ORMSession session;

	/**
	 * Get the instance to the singleton.
	 * 
	 * @return The instance.
	 */
	public static Encog getInstance() {
		if (instance == null) {
			instance = new Encog();
		}
		return instance;
	}

	/**
	 * Get the properties as a Map.
	 * @return The requested value.
	 */
	private final Map<String, String> properties = 
		new HashMap<String, String>();

	/**
	 * Private constructor.
	 */
	private Encog() {
		this.properties.put(Encog.ENCOG_VERSION, "1.2.0");
		this.properties.put(Encog.ENCOG_FILE_VERSION, "0");
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	public ORMSession getSession() {
		return session;
	}

	public void setSession(ORMSession session) {
		this.session = session;
	}
}
