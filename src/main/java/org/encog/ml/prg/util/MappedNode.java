package org.encog.ml.prg.util;

import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;

public class MappedNode {
	private final EncogProgram program;
	private final int index;
	private final int opcode;
	private final int size;
	private final List<MappedNode> children = new ArrayList<MappedNode>();
	private final ProgramExtensionTemplate template;
	
	public MappedNode(TraverseProgram trav) {
		this.program = trav.getProgram();
		this.index = trav.getFrameIndex();
		this.opcode = trav.getHeader().getOpcode();
		this.template = this.program.getContext().getFunctions().getOpCode(this.opcode);
		this.size = trav.getNextIndex() - trav.getFrameIndex();
	}

	/**
	 * @return the program
	 */
	public EncogProgram getProgram() {
		return program;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the opcode
	 */
	public int getOpcode() {
		return opcode;
	}


	/**
	 * @return the children
	 */
	public List<MappedNode> getChildren() {
		return children;
	}

	/**
	 * @return the template
	 */
	public ProgramExtensionTemplate getTemplate() {
		return template;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	
	

}
