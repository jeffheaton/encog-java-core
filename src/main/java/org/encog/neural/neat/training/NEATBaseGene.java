package org.encog.neural.neat.training;

import java.io.Serializable;

/**
 * Defines a base class for NEAT genes. A neat gene holds instructions on how to
 * create either a neuron or a link. The NEATLinkGene and NEATLinkNeuron classes
 * extend NEATBaseGene to provide this specific functionality.
 * 
 * The base gene defines those features that all NEAT genes share.  Particularly, 
 * this is the ability to have an innovation number.  These innovation numbers
 * allow NEAT genes to be effectively mapped for the crossover operator.
 * 
 */
public class NEATBaseGene implements Comparable<NEATBaseGene>, Serializable {
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 1L;

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
	@Override
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
	 * Set the id for this gene.
	 * 
	 * @param i
	 *            The id for this gene.
	 */
	public void setId(final long i) {
		this.id = i;
	}

	/**
	 * Set the innovation id for this gene.
	 * 
	 * @param theInnovationID
	 *            The innovation id for this gene.
	 */
	public void setInnovationId(final long theInnovationID) {
		innovationId = theInnovationID;
	}
}
