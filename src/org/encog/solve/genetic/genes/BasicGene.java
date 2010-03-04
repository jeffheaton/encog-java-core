package org.encog.solve.genetic.genes;

import org.encog.neural.networks.training.neat.NEATLinkGene;

public abstract class BasicGene implements Gene {

	private int id = -1;
	private int innovationId = -1;
	private boolean enabled = true;
	
	@Override
	public int getId() {
		return this.id ;
	}

	@Override
	public int getInnovationId() {
		return this.innovationId;
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean e) {
		this.enabled = e;		
	}

	@Override
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
