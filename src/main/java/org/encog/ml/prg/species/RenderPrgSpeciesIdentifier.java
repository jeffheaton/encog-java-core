package org.encog.ml.prg.species;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.parse.expression.CommonRender;
import org.encog.parse.expression.ExpressionNodeType;

public class RenderPrgSpeciesIdentifier extends CommonRender {
	private EncogProgram program;

	public RenderPrgSpeciesIdentifier() {
	}

	public String render(final EncogProgram theProgram) {
		this.program = theProgram;
		return renderNode(this.program.getRootNode());
	}

	private String renderNode(ProgramNode node) {
		StringBuilder result = new StringBuilder();
		
		ExpressionNodeType t = this.determineNodeType(node);
		
		for(int i=0;i<node.getChildNodes().size();i++) {
			ProgramNode childNode = node.getChildNode(i);
			if( result.length()>0 ) {
				result.append(" ");
			}
			result.append(renderNode(childNode));
		}

		if( result.length()>0 ) {
			result.append(" ");
		}
		
		if( t==ExpressionNodeType.ConstVal ) {
			result.append("[C]");
		} else if( t==ExpressionNodeType.Variable ) {
			result.append("[V]");
		} else if( t==ExpressionNodeType.Function || t==ExpressionNodeType.Operator) {
			result.append('[');
			result.append(node.getName());
			
			result.append(']');
		}
				
		return result.toString().trim();
	}
	
}
