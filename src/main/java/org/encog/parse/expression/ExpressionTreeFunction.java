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

import java.util.ArrayList;
import java.util.List;

public abstract class ExpressionTreeFunction extends ExpressionTreeElement {

	private final String name;
	private final List<ExpressionTreeElement> args = new ArrayList<ExpressionTreeElement>();
	private final ExpressionHolder owner;

	public ExpressionTreeFunction(final ExpressionHolder theOwner,
			final String theName, final List<ExpressionTreeElement> theArgs) {
		this.owner = theOwner;
		this.name = theName;
		this.args.addAll(theArgs);
	}

	public List<ExpressionTreeElement> getArgs() {
		return this.args;
	}

	public String getName() {
		return this.name;
	}

	public ExpressionHolder getOwner() {
		return this.owner;
	}
}
