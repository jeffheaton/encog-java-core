/*
 * Encog(tm) Core v2.6 - Java Version
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
package org.encog.ml.genetic.genes;

import org.encog.persist.annotations.EGAttribute;

/**
 * Implements the basic functionality for a gene. This is an abstract class.
 */
public abstract class BasicGene implements Gene {

	/**
	 * Is this gene enabled?
	 */
	@EGAttribute
	private boolean enabled = true;

	/**
	 * ID of this gene, -1 for unassigned.
	 */
	@EGAttribute
	private long id = -1;

	/**
	 * Innovation ID, -1 for unassigned.
	 */
	@EGAttribute
	private long innovationId = -1;

	/**
	 * {@inheritDoc}
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
