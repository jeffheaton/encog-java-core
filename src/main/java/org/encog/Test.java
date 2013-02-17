package org.encog;

import java.util.Random;

public class Test {
	public static void main(String[] args) {
		Random r = new Random();
		double min=0;
		double max=0;
		
		for(int i=0;i<100000000;i++) {
			double s = r.nextGaussian();
			min = Math.min(min, s);
			max = Math.max(max, s);
			System.out.println("min=" + min + ",max="+max);
			
		}
	}
}
