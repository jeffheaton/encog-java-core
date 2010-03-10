/*
 * Encog(tm) Core v2.4
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
package org.encog.solve.genetic.genes;

/**
 * Implements the basic functionality for a gene. This is an abstract class.
 */
public abstract class BasicGene implements Gene {

	/**
	 * Is this gene enabled?
	 */
	private boolean enabled = true;
	
	/**
	 * ID of this gene, -1 for unassigned.
	 */
	private long id = -1;
	
	/**
	 * Innovation ID, -1 for unassigned.
	 */
	private long innovationId = -1;

	/**
	 * Compare to another gene, sort by innovation id's.
	 */
	public int compareTo(final Gene o) {
		return ((int) (getInnovationId() - o.getInnovationId()));
	}

	/**
	 * @return The id of this gene.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return The innovation id of this gene.
	 */
	public long getInnovationId() {
		return innovationId;
	}

	/**
	 * @return True, if this gene is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param e True, if this gene is enabled.
	 */
	public void setEnabled(final boolean e) {
		enabled = e;
	}

	/**
	 * Set the id for this gene.
	 * @param id The id for this gene.
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * Set the innovation id for this gene.
	 * @param innovationID The innovation id for this gene.
	 */
	public void setInnovationId(final long innovationID) {
		innovationId = innovationID;
	}

}
