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
package org.encog.parse;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.parse.recognize.Recognize;
import org.encog.persist.EncogPersistedObject;
import org.encog.persist.Persistor;
import org.encog.persist.persistors.ParseTemplatePersistor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows templates to be specified for the parser.
 * @author jheaton
 *
 */
public class ParseTemplate implements EncogPersistedObject {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -2422158614923586260L;
	private Collection<Recognize> recognizers = new ArrayList<Recognize>();
	private String name;
	private String description;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	public void addRecognizer(Recognize recognize) {
		recognizers.add(recognize);
	}

	public Recognize createRecognizer(String name) {
		Recognize result = new Recognize(name);
		addRecognizer(result);
		return result;
	}

	/**
	 * @return the recognizers
	 */
	public Collection<Recognize> getRecognizers() {
		return recognizers;
	}

	public Persistor createPersistor() {
		return new ParseTemplatePersistor();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Object clone()
	{
		ParseTemplate result = new ParseTemplate();
		result.setName(this.getName());
		result.setDescription(this.getDescription());
		return result;
	}
}
