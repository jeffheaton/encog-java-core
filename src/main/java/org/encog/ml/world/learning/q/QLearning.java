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
package org.encog.ml.world.learning.q;

import org.encog.ml.world.Action;
import org.encog.ml.world.State;
import org.encog.ml.world.World;

public class QLearning {
	
	private final World world;
	/**
	 * The learning rate (alpha).
	 */
	private double learningRate;
	
	/**
	 * The discount rate (gamma).
	 */
	private double discountRate;
	
	public QLearning(World theWorld, double theLearningRate, double theDiscountRate) {
		this.world = theWorld;
		this.learningRate = theLearningRate;
		this.discountRate = theDiscountRate;
	}
	
	public void learn(State s1, Action a1, State s2, Action a2 ) {
		double q1 = this.world.getPolicyValue(s1, a1);
		double q2 = this.world.getPolicyValue(s2, a2);
		double r = s1.getReward();
		double d = q1+this.learningRate*(r+this.discountRate*q2-q1);
		this.world.setPolicyValue(s1, a1, d);
	}
}
