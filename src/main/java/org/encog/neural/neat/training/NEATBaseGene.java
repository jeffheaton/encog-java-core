package org.encog.neural.neat.training;

import java.io.Serializable;

public class NEATBaseGene implements Comparable<NEATBaseGene>, Serializable {
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

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
	 * {@inheritDoc}
	 */
	public int compareTo(final NEATBaseGene o) {
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
	 * @param i The id for this gene.
	 */
	public void setId(final long i) {
		this.id = i;
	}

	/**
	 * Set the innovation id for this gene.
	 * @param theInnovationID The innovation id for this gene.
	 */
	public void setInnovationId(final long theInnovationID) {
		innovationId = theInnovationID;
	}
}
