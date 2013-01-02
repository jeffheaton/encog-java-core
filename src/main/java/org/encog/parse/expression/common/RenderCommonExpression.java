package org.encog.parse.expression.common;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.exception.EncogEPLError;
import org.encog.ml.prg.exception.EncogProgramError;
import org.encog.ml.prg.extension.KnownConst;
import org.encog.ml.prg.extension.ProgramExtensionTemplate;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.util.TraverseProgram;
import org.encog.parse.expression.ExpressionNodeType;
import org.encog.util.datastruct.StackString;

public class RenderCommonExpression {
	private EncogProgram program;
	private TraverseProgram trav;
	private StackString stack = new StackString(100);

	public RenderCommonExpression() {
	}
	
	public String render(final EncogProgram theProgram) {
		this.program = theProgram;
		this.trav = new TraverseProgram(theProgram);
		this.trav.begin(0);
		return renderNode();
	}

	private void handleConst() {
		switch(this.trav.getHeader().getOpcode()) {
			case StandardExtensions.OPCODE_CONST_INT:
				stack.push(""+((int)trav.getHeader().getParam1()));
				break;
			case StandardExtensions.OPCODE_CONST_FLOAT:
				double d = this.trav.readDouble();
				stack.push(""+this.program.getContext().getFormat().format(d,32));
				break;
			default:
				stack.push("[Unknown Constant]");
				break;
		}
		
	}
	
	private void handleConstKnown() {
		ProgramExtensionTemplate temp = this.trav.getTemplate();
		stack.push(temp.getName());
	}

	private void handleVar() {
		int varIndex = (int)trav.getHeader().getParam2();
		stack.push(this.program.getVariables().getVariableName(varIndex));
	}
	
	private void handleFunction() {
		int opcode = this.trav.getHeader().getOpcode();
		ProgramExtensionTemplate temp = this.program.getContext().getFunctions().getOpCode(opcode);
		
		StringBuilder result = new StringBuilder();
		result.append(temp.getName());
		result.append('(');
		for(int i=0;i<temp.getChildNodeCount();i++) {
			if( i>0 ) {
				result.append(',');
			}
			result.append(this.stack.pop());
		}
		result.append(')');		
		this.stack.push(result.toString());
	}
	
	private void handleOperator() {
		int opcode = this.trav.getHeader().getOpcode();
		ProgramExtensionTemplate temp = this.program.getContext().getFunctions().getOpCode(opcode);
		
		StringBuilder result = new StringBuilder();
		
		if( this.trav.getTemplate().getChildNodeCount()==2 ) {
			String a = this.stack.pop();
			String b = this.stack.pop();
			result.append("(");
			result.append(b);
			result.append(temp.getName());
			result.append(a);
			result.append(")");
		} else if( this.trav.getTemplate().getChildNodeCount()==1 ) {
			String a = this.stack.pop();
			result.append("(");
			result.append(temp.getName());
			result.append(a);
			result.append(")");
		} else {
			throw new EncogProgramError("An operator must have an arity of 1 or 2, probably should be made a function.");
		}
		
		this.stack.push(result.toString());
	}

	public ExpressionNodeType determineNodeType() {
		int opcode = this.trav.getHeader().getOpcode();
		ProgramExtensionTemplate temp = this.program.getContext().getFunctions().getOpCode(opcode);
		
		if( temp instanceof KnownConst ) {
			return ExpressionNodeType.ConstKnown;
		}
		
		if (opcode==StandardExtensions.OPCODE_CONST_FLOAT || opcode==StandardExtensions.OPCODE_CONST_INT) {
			return ExpressionNodeType.ConstVal;
		}  
			
		if (opcode==StandardExtensions.OPCODE_VAR ) {
			return ExpressionNodeType.Variable;
		} 
		
		if( temp.isOperator() ) {
			return ExpressionNodeType.Operator;	
			
		}
		return ExpressionNodeType.Function;
			
	}

	private String renderNode() {		
		while(this.trav.next()) {
			switch (determineNodeType()) {
			case ConstVal:
				handleConst();
				break;
			case ConstKnown:
				handleConstKnown();
				break;
			case Operator:
				handleOperator();
				break;
			case Variable:
				handleVar();
				break;
			case Function:
				handleFunction();
				break;
			}
		}
		
		if( stack.size()>1 ) {
			throw new EncogEPLError("More than one value remains on stack after expression evaluation.");
		}
		return this.stack.pop();
	}
}
