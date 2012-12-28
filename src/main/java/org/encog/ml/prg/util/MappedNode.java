package org.encog.ml.prg.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.expvalue.ExpressionValue;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;

public class MappedNode implements Serializable {
	private final EncogProgram program;
	private final int index;
	private final int opcode;
	private final int param1;
	private final short param2;
	private final int size;
	private final List<MappedNode> children = new ArrayList<MappedNode>();
	private final ProgramExtensionTemplate template;
	
	public MappedNode(TraverseProgram trav) {
		this.program = trav.getProgram();
		this.index = trav.getFrameIndex();
		this.opcode = trav.getHeader().getOpcode();
		this.param1 = trav.getHeader().getParam1();
		this.param2 = trav.getHeader().getParam2();
		this.template = this.program.getContext().getFunctions().getOpCode(this.opcode);
		this.size = this.template.getInstructionSize(trav.getHeader());
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

	public boolean isNumericConst() {
		if( opcode==StandardExtensions.OPCODE_CONST_FLOAT || opcode==StandardExtensions.OPCODE_CONST_INT) {
			return true;
		}
		else return false;
	}

	public ExpressionValue getConstValue() {
		this.program.getStack().clear();
		this.program.setProgramCounter(this.index);
		this.program.readNodeHeader();
		this.template.evaluate(this.program);
		return this.program.getStack().pop();
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[MappedNode: opcode=");
		result.append(this.opcode);
		result.append("(");
		result.append(this.template.getName());
		result.append(")]");
		return result.toString();
	}

	public int getParam1() {
		return param1;
	}

	public short getParam2() {
		return param2;
	}
	
	

}
