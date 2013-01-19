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
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[SubstrateLink: source=");
		result.append(this.source.toString());
		result.append(",target=");
		result.append(this.target.toString());
		result.append("]");
		return result.toString();
	}
	
	
}
