/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main Encog class, does little more than provide version information.
 * Also used to hold the ORM session that Encog uses to work with
 * Hibernate.
 * 
 * @author jheaton
 */
public final class Encog {

	/**
	 * The default precision to use for compares.
	 */
	public static final int DEFAULT_PRECISION = 10;

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
	 * The current ORM session.
	 */
	private ORMSession session;

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
	private final Map<String, String> properties = 
		new HashMap<String, String>();

	/**
	 * Private constructor.
	 */
	private Encog() {
		this.properties.put(Encog.ENCOG_VERSION, "2.0.0");
		this.properties.put(Encog.ENCOG_FILE_VERSION, "1");
	}

	/**
	 * @return the properties
	 */
	public Map<String, String> getProperties() {
		return this.properties;
	}

	/**
	 * @return The ORM session that Encog is using.
	 */
	public ORMSession getSession() {
		return this.session;
	}

	/**
	 * Set the ORM session that Encog is to use.
	 * @param session An ORM session.
	 */
	public void setSession(final ORMSession session) {
		this.session = session;
	}
}
