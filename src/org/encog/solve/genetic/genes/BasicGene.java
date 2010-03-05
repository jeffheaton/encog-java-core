package org.encog.solve.genetic.genes;

import org.encog.neural.networks.training.neat.NEATLinkGene;

public abstract class BasicGene implements Gene {

	private int id = -1;
	private int innovationId = -1;
	private boolean enabled = true;
	
	public int getId() {
		return this.id ;
	}

	public int getInnovationId() {
		return this.innovationId;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean e) {
		this.enabled = e;		
	}

	public int compareTo(Gene o) {
		return( (int)(this.getInnovationId() - o.getInnovationId()) );
	}


	public void setId(int id) {
		this.id = id;
	}

	public void setInnovationId(int innovationID) {
		this.innovationId = innovationID;
	}
	
	

}
