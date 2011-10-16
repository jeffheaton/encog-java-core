package org.encog.ml.graph;

import org.encog.util.EngineArray;

public class EuclideanNode extends BasicNode {

	private final double[] data;
	
	public EuclideanNode(String label, double[] d) {
		super(label);
		this.data = EngineArray.arrayCopy(d);
	}
	
	public EuclideanNode(String label, double x, double y) {
		super(label);
		this.data = new double[2];
		this.data[0] = x;
		this.data[1] = y;
	}
	
	public double[] getData() {
		return data;
	}
	
	public static double distance(EuclideanNode p1, EuclideanNode p2) {
		return EngineArray.euclideanDistance(p1.getData(),p2.getData());
	}

}
