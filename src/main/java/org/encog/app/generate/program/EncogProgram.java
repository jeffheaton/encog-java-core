package org.encog.app.generate.program;


public class EncogProgram extends EncogTreeNode {

	public EncogProgram() {
		super(null, null);
		setProgram(this);
	}


	public EncogProgramNode createClass(String className) {
		EncogProgramNode node = new EncogProgramNode(this, this, NodeType.Class, className);
		this.getChildren().add(node);
		return node;
	}
	
	
}
