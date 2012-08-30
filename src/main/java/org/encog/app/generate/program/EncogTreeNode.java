package org.encog.app.generate.program;

import java.util.ArrayList;
import java.util.List;

public class EncogTreeNode {
	private final List<EncogProgramNode> children = new ArrayList<EncogProgramNode>();
	private final EncogTreeNode parent;
	private EncogProgram program;
	
	public EncogTreeNode(EncogProgram theProgram, EncogTreeNode theParent) {
		this.program = theProgram;
		this.parent = theParent;
	}
	
	public List<EncogProgramNode> getChildren() {
		return children;
	}

	public EncogTreeNode getParent() {
		return parent;
	}

	public EncogProgram getProgram() {
		return program;
	}
		
	public void setProgram(EncogProgram program) {
		this.program = program;
	}

	public void addComment(String str) {
		EncogProgramNode node = new EncogProgramNode(this.program, this, NodeType.Comment, str);
		this.children.add(node);
	}

	
}
