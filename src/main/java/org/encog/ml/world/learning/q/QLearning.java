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
