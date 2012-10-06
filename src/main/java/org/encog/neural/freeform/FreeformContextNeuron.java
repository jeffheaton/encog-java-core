package org.encog.neural.freeform;

import org.encog.neural.freeform.basic.BasicFreeformNeuron;

public class FreeformContextNeuron extends BasicFreeformNeuron {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 1L;
	private FreeformNeuron contextSource; 
	
	public FreeformContextNeuron(FreeformNeuron theContextSource) {
		super(null);
	}

	/**
	 * @return the contextSource
	 */
	public FreeformNeuron getContextSource() {
		return contextSource;
	}

	/**
	 * @param contextSource the contextSource to set
	 */
	public void setContextSource(FreeformNeuron contextSource) {
		this.contextSource = contextSource;
	}
	
	@Override
	public void updateContext() {
		this.setActivation(contextSource.getActivation());
	}

}
