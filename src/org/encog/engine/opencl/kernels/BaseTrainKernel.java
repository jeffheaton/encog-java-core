package org.encog.engine.opencl.kernels;

import org.encog.engine.data.EngineIndexableSet;
import org.encog.engine.network.flat.FlatNetwork;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.engine.util.ResourceLoader;
import org.jocl.cl_mem;

public class BaseTrainKernel extends EncogKernel {

	/**
	 * A buffer to communicate weights to the kernel.
	 */
	private cl_mem weightInArrayBuffer;

	/**
	 * A buffer to communicate weights from the kernel.
	 */
	private cl_mem weightOutArrayBuffer;

	/**
	 * A buffer to hold the layer index.
	 */
	private cl_mem layerIndexBuffer;

	/**
	 * A buffer to hold the layer counts.
	 */
	private cl_mem layerCountBuffer;

	/**
	 * A buffer to hold the layer feed counts.
	 */
	private cl_mem layerFeedCountBuffer;

	/**
	 * A buffer to hold the weight indexes.
	 */
	private cl_mem weightIndexBuffer;

	/**
	 * A buffer to hold the activations for each of the layers.
	 */
	private cl_mem activationTypeBuffer;

	/**
	 * A buffer to hold the slope for the activation of each of the layers.
	 */
	private cl_mem slopeBuffer;

	private cl_mem tempDataInBuffer;

	private cl_mem tempDataOutBuffer;

	/**
	 * The weight and bias array for the network.
	 */
	private float[] weightInArray;

	private float[] weightOutArray;

	private float[] tempDataArray;

	/**
	 * The slopes.
	 */
	private float[] slopeArray;

	/**
	 * An array to hold the input to the neural network.
	 */
	private float[] inputArray;

	/**
	 * An array to hold the ideal values expected from the network.
	 */
	private float[] idealArray;

	/**
	 * The input buffer.
	 */
	private cl_mem inputBuffer;

	/**
	 * The ideal buffer.
	 */
	private cl_mem idealBuffer;

	/**
	 * Holds parameters passed to the kernel.
	 */
	private int[] paramArray;

	/**
	 * A buffer to hold the parameters.
	 */
	private cl_mem paramBuffer;

	/**
	 * A buffer to hold the errors.
	 */
	private cl_mem errorBuffer;

	/**
	 * A buffer to hold the gradients.
	 */
	private cl_mem gradientBuffer;

	private final FlatNetwork flat;

	/**
	 * The training errors for this workload.
	 */
	private float[] errors;
	
	private final EngineIndexableSet training;

	/**
	 * Create an Encog OpenCL kernel. The Kernel will be loaded from an embedded
	 * resource.
	 * 
	 * @param device
	 *            The OpenCL device to use.
	 * @param sourceName
	 *            The name of the kernel, from an embedded resource.
	 * @param kernelName
	 *            The name of the function, in the kernel, called to start the
	 *            kernel.
	 */
	public BaseTrainKernel(final FlatNetwork flat,
			final EngineIndexableSet training,
			final EncogCLDevice device, final String sourceName,
			final String kernelName) {
		super(device,sourceName,kernelName);
		this.training = training;
		this.flat = flat;
	}
	
	/**
	 * @return the weightInArrayBuffer
	 */
	public cl_mem getWeightInArrayBuffer() {
		return weightInArrayBuffer;
	}

	/**
	 * @param weightInArrayBuffer the weightInArrayBuffer to set
	 */
	public void setWeightInArrayBuffer(cl_mem weightInArrayBuffer) {
		this.weightInArrayBuffer = weightInArrayBuffer;
	}

	/**
	 * @return the weightOutArrayBuffer
	 */
	public cl_mem getWeightOutArrayBuffer() {
		return weightOutArrayBuffer;
	}

	/**
	 * @param weightOutArrayBuffer the weightOutArrayBuffer to set
	 */
	public void setWeightOutArrayBuffer(cl_mem weightOutArrayBuffer) {
		this.weightOutArrayBuffer = weightOutArrayBuffer;
	}

	/**
	 * @return the layerIndexBuffer
	 */
	public cl_mem getLayerIndexBuffer() {
		return layerIndexBuffer;
	}

	/**
	 * @param layerIndexBuffer the layerIndexBuffer to set
	 */
	public void setLayerIndexBuffer(cl_mem layerIndexBuffer) {
		this.layerIndexBuffer = layerIndexBuffer;
	}

	/**
	 * @return the layerCountBuffer
	 */
	public cl_mem getLayerCountBuffer() {
		return layerCountBuffer;
	}

	/**
	 * @param layerCountBuffer the layerCountBuffer to set
	 */
	public void setLayerCountBuffer(cl_mem layerCountBuffer) {
		this.layerCountBuffer = layerCountBuffer;
	}

	/**
	 * @return the layerFeedCountBuffer
	 */
	public cl_mem getLayerFeedCountBuffer() {
		return layerFeedCountBuffer;
	}

	/**
	 * @param layerFeedCountBuffer the layerFeedCountBuffer to set
	 */
	public void setLayerFeedCountBuffer(cl_mem layerFeedCountBuffer) {
		this.layerFeedCountBuffer = layerFeedCountBuffer;
	}

	/**
	 * @return the weightIndexBuffer
	 */
	public cl_mem getWeightIndexBuffer() {
		return weightIndexBuffer;
	}

	/**
	 * @param weightIndexBuffer the weightIndexBuffer to set
	 */
	public void setWeightIndexBuffer(cl_mem weightIndexBuffer) {
		this.weightIndexBuffer = weightIndexBuffer;
	}

	/**
	 * @return the activationTypeBuffer
	 */
	public cl_mem getActivationTypeBuffer() {
		return activationTypeBuffer;
	}

	/**
	 * @param activationTypeBuffer the activationTypeBuffer to set
	 */
	public void setActivationTypeBuffer(cl_mem activationTypeBuffer) {
		this.activationTypeBuffer = activationTypeBuffer;
	}

	/**
	 * @return the slopeBuffer
	 */
	public cl_mem getSlopeBuffer() {
		return slopeBuffer;
	}

	/**
	 * @param slopeBuffer the slopeBuffer to set
	 */
	public void setSlopeBuffer(cl_mem slopeBuffer) {
		this.slopeBuffer = slopeBuffer;
	}

	/**
	 * @return the tempDataInBuffer
	 */
	public cl_mem getTempDataInBuffer() {
		return tempDataInBuffer;
	}

	/**
	 * @param tempDataInBuffer the tempDataInBuffer to set
	 */
	public void setTempDataInBuffer(cl_mem tempDataInBuffer) {
		this.tempDataInBuffer = tempDataInBuffer;
	}

	/**
	 * @return the tempDataOutBuffer
	 */
	public cl_mem getTempDataOutBuffer() {
		return tempDataOutBuffer;
	}

	/**
	 * @param tempDataOutBuffer the tempDataOutBuffer to set
	 */
	public void setTempDataOutBuffer(cl_mem tempDataOutBuffer) {
		this.tempDataOutBuffer = tempDataOutBuffer;
	}

	/**
	 * @return the weightInArray
	 */
	public float[] getWeightInArray() {
		return weightInArray;
	}

	/**
	 * @param weightInArray the weightInArray to set
	 */
	public void setWeightInArray(float[] weightInArray) {
		this.weightInArray = weightInArray;
	}

	/**
	 * @return the weightOutArray
	 */
	public float[] getWeightOutArray() {
		return weightOutArray;
	}

	/**
	 * @param weightOutArray the weightOutArray to set
	 */
	public void setWeightOutArray(float[] weightOutArray) {
		this.weightOutArray = weightOutArray;
	}

	/**
	 * @return the tempDataArray
	 */
	public float[] getTempDataArray() {
		return tempDataArray;
	}

	/**
	 * @param tempDataArray the tempDataArray to set
	 */
	public void setTempDataArray(float[] tempDataArray) {
		this.tempDataArray = tempDataArray;
	}

	/**
	 * @return the slopeArray
	 */
	public float[] getSlopeArray() {
		return slopeArray;
	}

	/**
	 * @param slopeArray the slopeArray to set
	 */
	public void setSlopeArray(float[] slopeArray) {
		this.slopeArray = slopeArray;
	}

	/**
	 * @return the inputBuffer
	 */
	public cl_mem getInputBuffer() {
		return inputBuffer;
	}

	/**
	 * @param inputBuffer the inputBuffer to set
	 */
	public void setInputBuffer(cl_mem inputBuffer) {
		this.inputBuffer = inputBuffer;
	}

	/**
	 * @return the idealBuffer
	 */
	public cl_mem getIdealBuffer() {
		return idealBuffer;
	}

	/**
	 * @param idealBuffer the idealBuffer to set
	 */
	public void setIdealBuffer(cl_mem idealBuffer) {
		this.idealBuffer = idealBuffer;
	}

	/**
	 * @return the paramArray
	 */
	public int[] getParamArray() {
		return paramArray;
	}

	/**
	 * @param paramArray the paramArray to set
	 */
	public void setParamArray(int[] paramArray) {
		this.paramArray = paramArray;
	}

	/**
	 * @return the paramBuffer
	 */
	public cl_mem getParamBuffer() {
		return paramBuffer;
	}

	/**
	 * @param paramBuffer the paramBuffer to set
	 */
	public void setParamBuffer(cl_mem paramBuffer) {
		this.paramBuffer = paramBuffer;
	}

	/**
	 * @return the errorBuffer
	 */
	public cl_mem getErrorBuffer() {
		return errorBuffer;
	}

	/**
	 * @param errorBuffer the errorBuffer to set
	 */
	public void setErrorBuffer(cl_mem errorBuffer) {
		this.errorBuffer = errorBuffer;
	}

	/**
	 * @return the gradientBuffer
	 */
	public cl_mem getGradientBuffer() {
		return gradientBuffer;
	}

	/**
	 * @param gradientBuffer the gradientBuffer to set
	 */
	public void setGradientBuffer(cl_mem gradientBuffer) {
		this.gradientBuffer = gradientBuffer;
	}

	/**
	 * @return the errors
	 */
	public float[] getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(float[] errors) {
		this.errors = errors;
	}

	/**
	 * @return the training
	 */
	public EngineIndexableSet getTraining() {
		return training;
	}


	/**
	 * @return the inputArray
	 */
	public float[] getInputArray() {
		return inputArray;
	}

	/**
	 * @return the idealArray
	 */
	public float[] getIdealArray() {
		return idealArray;
	}

	/**
	 * @return the flat
	 */
	public FlatNetwork getFlat() {
		return flat;
	}

	/**
	 * @param inputArray the inputArray to set
	 */
	public void setInputArray(float[] inputArray) {
		this.inputArray = inputArray;
	}

	/**
	 * @param idealArray the idealArray to set
	 */
	public void setIdealArray(float[] idealArray) {
		this.idealArray = idealArray;
	}

	public void setArgs()
	{
		setArg(0,this.paramBuffer);
		setArg(1,this.errorBuffer);
		setArg(2,this.layerIndexBuffer);
		setArg(3,this.layerCountBuffer);
		setArg(4,this.layerFeedCountBuffer);
		setArg(5,this.weightIndexBuffer);
		setArg(6,this.inputBuffer);
		setArg(7,this.idealBuffer);
		setArg(8,this.weightInArrayBuffer);
		setArg(9,this.weightOutArrayBuffer);
		setArg(10,this.gradientBuffer);
		setArg(11,this.activationTypeBuffer);
		setArg(12,this.slopeBuffer);
		setArg(13,this.tempDataInBuffer);
		setArg(14,this.tempDataOutBuffer);
	}
	
}
