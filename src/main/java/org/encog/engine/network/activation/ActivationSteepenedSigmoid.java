package org.encog.engine.network.activation;

/**
 * The Steepened Sigmoid is an activation function typically used with NEAT.
 * 
 * Valid derivative calculated with the R package, so this does work with 
 * non-NEAT networks too.
 * 
 * It was developed by  Ken Stanley while at The University of Texas at Austin.
 * http://www.cs.ucf.edu/~kstanley/
 */
public class ActivationSteepenedSigmoid implements ActivationFunction {

    /**
     * The parameters.
     */
    private final double[] params;

    /**
     * Construct a basic HTAN activation function, with a slope of 1.
     */
    public ActivationSteepenedSigmoid() {
        this.params = new double[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void activationFunction(final double[] x, final int start,
            final int size) {
        for (int i = start; i < start + size; i++) {
        	x[i] = 1.0/(1.0 + Math.exp(-4.9*x[i]));
        }
    }

    /**
     * @return The object cloned;
     */
    @Override
    public final ActivationFunction clone() {
        return new ActivationSteepenedSigmoid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double derivativeFunction(final double b, final double a) {
    	double s = Math.exp(-4.9 * a);
    	return Math.pow(s * 4.9/(1 + s),2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String[] getParamNames() {
        final String[] result = { };
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final double[] getParams() {
        return this.params;
    }

    /**
     * @return Return true, Elliott activation has a derivative.
     */
    @Override
    public final boolean hasDerivative() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setParam(final int index, final double value) {
        this.params[index] = value;
    }

}
