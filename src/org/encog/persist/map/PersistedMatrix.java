package org.encog.persist.map;

import org.encog.mathutil.matrices.Matrix;

public class PersistedMatrix extends PersistedProperty {

	private Matrix matrix;
	
	public PersistedMatrix(Matrix matrix) {
		super(false);
		this.matrix = matrix;
	}

	@Override
	public Object getData() {
		return matrix;
	}

	@Override
	public String getString() {
		return matrix.toString();
	}

	public Matrix getMatrix() {		
		return matrix;
	}

}
