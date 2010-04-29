package org.encog.mathutil.randomize;

/**
 * Generally, you will not want to use this randomizer as a pure neural network
 * randomizer. More on this later in the description.
 * 
 * Generate random numbers that fall within a Gaussian curve. The mean
 * represents the center of the curve, and the standard deviation helps
 * determine the length of the curve on each side.
 * 
 * This randomizer is used mainly for special cases where I want to generate
 * random numbers in a Gaussian range. For a pure neural network initilizer, it
 * leaves much to be desired. Typically this randomizer provides the worst
 * performance of any Encog randomizer.
 * 
 * Uses the "Box Muller" method.
 * http://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
 * 
 * Ported from C++ version provided by Everett F. Carter Jr., 1994
 */
public class GaussianRandomizer extends BasicRandomizer {

	/**
	 * The y2 value.
	 */
	private double y2;
	
	/**
	 * Should we use the last value.
	 */
	private boolean useLast = false;
	
	/**
	 * The mean.
	 */
	private double mean;
	
	/**
	 * The standard deviation.
	 */
	private double standardDeviation;

	/**
	 * Compute a Gaussian random number.
	 * @param m The mean.
	 * @param s The standard deviation.
	 * @return The random number.
	 */
	public double boxMuller(double m, double s) {
		double x1, x2, w, y1;

		// use value from previous call
		if (useLast) {
			y1 = y2;
			useLast = false;
		} else {
			do {
				x1 = 2.0 * Math.random() - 1.0;
				x2 = 2.0 * Math.random() - 1.0;
				w = x1 * x1 + x2 * x2;
			} while (w >= 1.0);

			w = Math.sqrt((-2.0 * Math.log(w)) / w);
			y1 = x1 * w;
			y2 = x2 * w;
			useLast = true;
		}

		return (m + y1 * s);
	}

	/**
	 * Construct a Gaussian randomizer.  The mean, the standard deviation.
	 * @param mean The mean.
	 * @param standardDeviation The standard deviation.
	 */
	public GaussianRandomizer(double mean, double standardDeviation) {
		this.mean = mean;
		this.standardDeviation = standardDeviation;
	}

	/**
	 * Generate a random number.
	 * @param d The input value, not used.
	 * @return The random number.
	 */
	public double randomize(double d) {
		return boxMuller(this.mean, this.standardDeviation);
	}

}
