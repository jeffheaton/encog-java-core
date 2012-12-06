package org.encog.parse.expression.rpn;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.extension.KnownConst;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.parse.expression.ExpressionNodeType;

public class RenderRPN {
	private EncogProgram program;
	private OpCodeHeader header = new OpCodeHeader();
	private StringBuilder result = new StringBuilder();

	public RenderRPN() {
	}

	public String render(final EncogProgram theProgram) {
		this.program = theProgram;
		this.program.setProgramCounter(0);
		this.result.setLength(0);
		return renderNode();
	}

	private void handleConst() {
		switch (this.header.getOpcode()) {
		case StandardExtensions.OPCODE_CONST_INT:
			result.append("" + ((int) this.header.getParam1()));
			break;
		case StandardExtensions.OPCODE_CONST_FLOAT:
			double d = this.program.readDouble();
			result.append(""
					+ this.program.getContext().getFormat().format(d, 32));
			break;
		default:
			result.append("[Unknown Constant]");
			break;
		}

	}

	private void handleConstKnown() {
		ProgramExtensionTemplate temp = this.program.getContext()
				.getFunctions().getOpCode(this.header.getOpcode());
		result.append(temp.getName());
	}

	private void handleVar() {
		int varIndex = (int) this.header.getParam2();
		result.append(this.program.getVariables().getVariableName(varIndex));
	}

	private void handleFunction() {
		int opcode = this.header.getOpcode();
		ProgramExtensionTemplate temp = this.program.getContext()
				.getFunctions().getOpCode(opcode);

		this.result.append("[");
		this.result.append(temp.getName());
		this.result.append("]");
	}

	public ExpressionNodeType determineNodeType() {
		this.program.readNodeHeader(this.header);
		int opcode = this.header.getOpcode();
		ProgramExtensionTemplate temp = this.program.getContext()
				.getFunctions().getOpCode(opcode);

		if (temp instanceof KnownConst) {
			return ExpressionNodeType.ConstKnown;
		}

		if (opcode == StandardExtensions.OPCODE_CONST_FLOAT
				|| opcode == StandardExtensions.OPCODE_CONST_INT) {
			return ExpressionNodeType.ConstVal;
		}

		if (opcode == StandardExtensions.OPCODE_VAR) {
			return ExpressionNodeType.Variable;
		}

		if (temp.getChildNodeCount() != 2) {
			return ExpressionNodeType.Function;
		}

		String name = temp.getName();

		if (!Character.isLetterOrDigit(name.charAt(0))) {
			return ExpressionNodeType.Operator;
		}

		return ExpressionNodeType.Function;
	}

	private String renderNode() {
		while (!this.program.eof()) {
			switch (determineNodeType()) {
			case ConstVal:
				handleConst();
				break;
			case ConstKnown:
				handleConstKnown();
				break;
			case Variable:
				handleVar();
				break;
			case Operator:
			case Function:
				handleFunction();
				break;
			}
			this.result.append(" ");
		}

		return this.result.toString().trim();
	}
}
