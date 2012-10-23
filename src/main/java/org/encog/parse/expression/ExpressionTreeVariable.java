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
package org.encog.parse.expression;

import org.encog.parse.expression.expvalue.ExpressionValue;

public class ExpressionTreeVariable extends ExpressionTreeElement {
	final private ExpressionHolder owner;
	final private String name;

	public ExpressionTreeVariable(final ExpressionHolder theOwner,
			final String theName) {
		this.owner = theOwner;
		this.name = theName;
	}

	@Override
	public ExpressionValue evaluate() {
		if (!this.owner.variableExists(this.name)) {
			throw new ExpressionError("Undefined variable: " + this.name);
		}

		return this.owner.get(this.name);
	}

}
