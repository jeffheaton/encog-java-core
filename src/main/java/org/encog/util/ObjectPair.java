/*
 * Encog(tm) Core v3.1 - Java Version
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
package org.encog.util;

/**
 * A pair of objects.
 *
 * @param <A> The first object type.
 * @param <B> The second object type.
 */
public class ObjectPair<A, B> {

	/**
	 * The first object.
	 */
	private final A a;
	
	/**
	 * The second object.
	 */
	private final B b;

	/**
	 * Construct an object pair.
	 * @param a The first object.
	 * @param b The second object.
	 */
	public ObjectPair(final A a, final B b) {
		super();
		this.a = a;
		this.b = b;
	}

	/**
	 * @return The first object.
	 */
	public A getA() {
		return this.a;
	}

	/**
	 * @return The second object.
	 */
	public B getB() {
		return this.b;
	}

}
