/*
 * Encog(tm) Core v3.2 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.app.analyst.csv.process;

import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.ml.prg.extension.BasicTemplate;
import org.encog.ml.prg.extension.FunctionFactory;

public class FunctionFieldMaxPIP extends BasicTemplate {
	
	private ProcessExtension extension;
	
	public FunctionFieldMaxPIP(ProcessExtension theExtension) {
		super("fieldmaxpip",3,true);
		this.setOpcode((short)(FunctionFactory.ENCOG_EXTRA_OPCODES+2));
		this.extension = theExtension;
	}
	
	@Override
	public void evaluate(EncogProgram prg) {
		int stopIndex = (int)prg.getStack().pop().toIntValue();//0
		int startIndex = (int)prg.getStack().pop().toIntValue();//1
		String fieldName = prg.getStack().pop().toStringValue();//2
		
		int value = Integer.MIN_VALUE;
		
		String str = this.extension.getField(fieldName,this.extension.getBackwardWindowSize());
		double quoteNow = extension.getFormat().parse(str);
		
		for(int i=startIndex;i<=stopIndex;i++) {
			str = this.extension.getField(fieldName,this.extension.getBackwardWindowSize()+i);
			double d = extension.getFormat().parse(str)-quoteNow;
			d/=0.0001;
			d=Math.round(d);
			value = Math.max((int)d, value);
		}
		
		
		prg.getStack().push(value);
	}

	@Override
	public int getInstructionSize(OpCodeHeader header) {
		return 1;
	}


}
