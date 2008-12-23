package org.encog.neural.networks.layers;

import org.encog.EncogError;
import org.encog.matrix.Matrix;
import org.encog.matrix.MatrixMath;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationSigmoid;
import org.encog.neural.data.NeuralData;
import org.encog.neural.networks.Layer;

public class RecurrantLayer extends FeedforwardLayer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -778649153858552290L;

	public enum RecurrantSource {
		SELF,
		OUTPUT
	} ;
	
	private RecurrantSource recurrantSource;
	private Synapse recurrantSynapse; 
	
	public RecurrantLayer(ActivationFunction thresholdFunction, int neuronCount, RecurrantSource recurrantSource) {
		super(thresholdFunction, neuronCount);

		this.recurrantSource = recurrantSource;
		
		switch( this.recurrantSource )
		{
			case SELF:
				this.recurrantSynapse = new Synapse(neuronCount,neuronCount);
				break;
			case OUTPUT:
				// will need to be created later, the output
				// layer may not even exist yet.
				break;
		}

	}
	
	public RecurrantLayer(int neuronCount, RecurrantSource source) {
		this(new ActivationSigmoid(),neuronCount,source);
	}
	
	private Layer findOutput()
	{
		Layer result = this;
		while( result.getNext()!=null )
		{
			result = result.getNext();
		}
		return result;
	}
	
	/**
	 * Set the next layer.
	 * 
	 * @param next
	 *            the next layer.
	 */
	@Override
	public void setNext(final Layer next) {
		super.setNext(next);
		if( this.recurrantSource == RecurrantSource.OUTPUT )
		{
			Layer output = findOutput();
			if( (this.recurrantSynapse==null) ||
				(output.getNeuronCount() != this.recurrantSynapse.getNeuronCount()) )
			{
				this.recurrantSynapse = new Synapse(output.getNeuronCount(),this.getNeuronCount());
			}
		}
	}
	


	public RecurrantSource getRecurrantSource() {
		return recurrantSource;
	}
	
	/**
	 * Compute the outputs for this layer given the input pattern. The output is
	 * also stored in the fire instance variable.
	 * 
	 * @param pattern
	 *            The input pattern.
	 * @return The output from this layer.
	 */
	public NeuralData compute(final NeuralData pattern) {
		
		Layer recurrantLayer = null;

		switch( this.recurrantSource )
		{
			case SELF:
				recurrantLayer = this;
				break;
			case OUTPUT:
				recurrantLayer = findOutput();
				break;
			default:
				throw new EncogError("Unsupported recurrant sorce");
		}
		
		final Matrix inputRecurrant = createInputMatrix(recurrantLayer.getFire());
		
		int i;
		if (pattern != null) {
			for (i = 0; i < getNeuronCount(); i++) {
				setFire(i, pattern.getData(i));
			}
		}

		final Matrix inputThis = createInputMatrix(this.getFire());
				
		for (i = 0; i < getNext().getNeuronCount(); i++) {
			final Matrix colThis = getMatrix().getCol(i);
			final Matrix colRecurrant = recurrantSynapse.getMatrix().getCol(i);
			
			final double sumThis = MatrixMath.dotProduct(colThis, inputThis);
			final double sumRecurrant = MatrixMath.dotProduct(colRecurrant, inputRecurrant);
			
			getNext().setFire(i,
					this.getActivationFunction().activationFunction(sumThis+sumRecurrant));
		}

		return this.getFire();
	}
	
	public void reset() {
		super.reset();
		this.recurrantSynapse.getMatrix().ramdomize(-1, 1);
	}
	
	/**
	 * @return The string form of the layer.
	 */
	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append("[RecurrantLayer: Neuron Count=");
		result.append(getNeuronCount());
		result.append(", source=");
		result.append(this.recurrantSource);
		result.append("]");
		return result.toString();
	}

}
