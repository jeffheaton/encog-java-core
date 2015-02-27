/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
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
			ValueType t = node.getData()[i].getExpressionType();
			if( t==ValueType.booleanType) {
				result.append(node.getData()[i].toBooleanValue()?'t':'f');
			} else if( t==ValueType.floatingType) {
				result.append(CSVFormat.EG_FORMAT.format(node.getData()[i].toFloatValue(), Encog.DEFAULT_PRECISION));
			} else if( t==ValueType.intType) {
				result.append(node.getData()[i].toIntValue());
			} else if( t==ValueType.enumType) {
				result.append(node.getData()[i].getEnumType());
				result.append("#");
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
