package org.encog.parse.expression.common;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;

public class RenderCommonExpression {
	private EncogProgram holder;

	public RenderCommonExpression() {
	}
	
	public String render(final EncogProgram theHolder) {
		this.holder = theHolder;
		ProgramNode node = theHolder.getExpressions().get(0);
		return renderNode(node);
	}
	
	private String renderNode(ProgramNode node) {
		StringBuilder result = new StringBuilder();
		result.append("(");
		result.append(renderNode(node.getChildNodes().get(0)));
		result.append(node.getName());
		result.append(renderNode(node.getChildNodes().get(1)));
		result.append(")");
		return result.toString();
	}
}
