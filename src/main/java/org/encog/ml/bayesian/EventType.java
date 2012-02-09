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
package org.encog.ml.bayesian;

/**
 * The type of event for a Bayesian Network.
 */
public enum EventType {
	/**
	 * The event is used as evidence to predict the outcome.
	 */
	Evidence,
	
	/**
	 * This event is neither evidence our outcome, but still 
	 * is involved in the Bayesian Graph.
	 */
	Hidden,
	
	/**
	 * The event is outcome, which means that we would like to get
	 * a value for given evidence.
	 */
	Outcome
}
