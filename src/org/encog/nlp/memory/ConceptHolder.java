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
package org.encog.nlp.memory;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * Note: This class is part of the Encog Natural Language Processing(NLP)
 * package.  This package is still under heavy construction, and will not 
 * be considered stable until Encog 3.0.
 * 
 * @author jheaton
 *
 */
public class ConceptHolder {
	private final List<VarConcept> concepts = new ArrayList<VarConcept>();
	private long serialNumber = 1;
	
	/**
	 * The logging object.
	 */
	@SuppressWarnings("unused")
	final private Logger logger = LoggerFactory.getLogger(this.getClass());

	/** Creates a new instance of LongConceptHolder */
	public ConceptHolder() {
	}

	public VarConcept create() {
		return create("");
	}

	public synchronized VarConcept create(final String contents) {
		VarConcept result = null;
		if (contents.length() > 0) {
			result = find(contents);
		}
		if (result == null) {
			result = new VarConcept(this.serialNumber++, contents);
			this.concepts.add(result);
		}
		return result;
	}

	public Concept find(final long serialNumber)
			throws ConceptNotFoundError {
		Concept result = null;

		if (serialNumber > 0) {
			for (final VarConcept concept : this.concepts) {
				if (concept.getSerialNumber() == serialNumber) {
					result = concept;
					break;
				}
			}
		} else if (serialNumber < 0) {
			result = ConstConcept.find(serialNumber);
		} else if (serialNumber == 0) {
			result = null;
		}

		return result;

	}

	private VarConcept find(final String contents) {
		VarConcept result = null;

		// first see if it already exists
		final Object list[] = this.concepts.toArray();
		for (int i = 0; i < list.length; i++) {
			final VarConcept listConcept = (VarConcept) list[i];
			if (listConcept.equals(contents)) {
				result = listConcept;
				break;
			}
		}
		return result;
	}

	public List<VarConcept> getConcepts() {
		return this.concepts;
	}

	public void load(final VarConcept input) {
		this.concepts.add(input);
	}
}
