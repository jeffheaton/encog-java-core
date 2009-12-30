/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010, Heaton Research Inc., and individual contributors.
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
package org.encog.normalize.output;

import java.util.ArrayList;
import java.util.Collection;

import org.encog.persist.annotations.EGReference;

/**
 * Provides very basic functionality that other output field groups
 * will use.  Mainly provides the list of fields that are grouped.
 *
 */
public abstract class BasicOutputFieldGroup implements OutputFieldGroup {

	/**
	 * The fields in this group.
	 */
	@EGReference
	private final Collection<OutputFieldGrouped> fields = 
		new ArrayList<OutputFieldGrouped>();

	/**
	 * Add a field to this group.
	 * @param field The field to add to the group.
	 */
	public void addField(final OutputFieldGrouped field) {
		this.fields.add(field);
	}

	/**
	 * @return The list of grouped fields.
	 */
	public Collection<OutputFieldGrouped> getGroupedFields() {
		return this.fields;
	}
}
