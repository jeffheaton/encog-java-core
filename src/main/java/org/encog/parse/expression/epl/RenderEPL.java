package org.encog.parse.expression.epl;

import org.encog.Encog;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.ProgramNode;
import org.encog.ml.prg.expvalue.ValueType;
import org.encog.parse.expression.CommonRender;
import org.encog.util.csv.CSVFormat;

public class RenderEPL extends CommonRender {
	private EncogProgram program;

	public RenderEPL() {
	}

	public String render(final EncogProgram theProgram) {
		this.program = theProgram;
		return renderNode(this.program.getRootNode());
	}
	
	private String renderNode(ProgramNode node) {
		StringBuilder result = new StringBuilder();

		for(int i=0;i<node.getChildNodes().size();i++) {
			ProgramNode childNode = node.getChildNode(i);
			result.append(renderNode(childNode));
		}
		
		result.append('[');
		result.append(node.getName());
		result.append(':');
		result.append(node.getTemplate().getChildNodeCount());
		
		for(int i=0;i<node.getTemplate().getDataSize();i++) {
			result.append(':');
			ValueType t = node.getData()[i].getCurrentType();
			if( t==ValueType.booleanType) {
				result.append(node.getData()[i].toBooleanValue()?'t':'f');
			} else if( t==ValueType.floatingType) {
				result.append(CSVFormat.EG_FORMAT.format(node.getData()[i].toFloatValue(), Encog.DEFAULT_PRECISION));
			} else if( t==ValueType.intType) {
				result.append(node.getData()[i].toIntValue());
			} else if( t==ValueType.stringType) {
				result.append("\"");
				result.append(node.getData()[i].toStringValue());
				result.append("\"");
			}
		}
		result.append(']');
				
		return result.toString().trim();
	}
	
}
