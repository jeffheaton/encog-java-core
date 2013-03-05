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

	public MappedNode(final TraverseProgram trav) {
		this.program = trav.getProgram();
		this.index = trav.getFrameIndex();
		this.opcode = trav.getHeader().getOpcode();
		this.param1 = trav.getHeader().getParam1();
		this.param2 = trav.getHeader().getParam2();
		this.template = this.program.getContext().getFunctions()
				.getOpCode(this.opcode);
		this.size = this.template.getInstructionSize(trav.getHeader());
	}

	/**
	 * @return the children
	 */
	public List<MappedNode> getChildren() {
		return this.children;
	}

	public ExpressionValue getConstValue() {
		this.program.getStack().clear();
		this.program.setProgramCounter(this.index);
		this.program.readNodeHeader();
		this.template.evaluate(this.program);
		return this.program.getStack().pop();
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * @return the opcode
	 */
	public int getOpcode() {
		return this.opcode;
	}

	public int getParam1() {
		return this.param1;
	}

	public short getParam2() {
		return this.param2;
	}

	/**
	 * @return the program
	 */
	public EncogProgram getProgram() {
		return this.program;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * @return the template
	 */
	public ProgramExtensionTemplate getTemplate() {
		return this.template;
	}

	public boolean isNumericConst() {
		if (this.opcode == StandardExtensions.OPCODE_CONST_FLOAT
				|| this.opcode == StandardExtensions.OPCODE_CONST_INT) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[MappedNode: opcode=");
		result.append(this.opcode);
		result.append("(");
		result.append(this.template.getName());
		result.append(")]");
		return result.toString();
	}

}
