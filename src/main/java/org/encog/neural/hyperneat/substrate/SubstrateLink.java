package org.encog.neural.hyperneat.substrate;

public class SubstrateLink {
	private final SubstrateNode source;
	private final SubstrateNode target;
	
	public SubstrateLink(SubstrateNode source, SubstrateNode target) {
		super();
		this.source = source;
		this.target = target;
	}

	/**
	 * @return the source
	 */
	public SubstrateNode getSource() {
		return source;
	}

	/**
	 * @return the target
	 */
	public SubstrateNode getTarget() {
		return target;
	}
	
	
	
}
