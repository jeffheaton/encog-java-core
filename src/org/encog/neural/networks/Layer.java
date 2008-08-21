/*
  * Encog Neural Network and Bot Library for Java v1.x
  * http://www.heatonresearch.com/encog/
  * http://code.google.com/p/encog-java/
  * 
  * Copyright 2008, Heaton Research Inc., and individual contributors.
  * See the copyright.txt in the distribution for a full listing of 
  * individual contributors.
  *
  * This is free software; you can redistribute it and/or modify it
  * under the terms of the GNU Lesser General Public License as
  * published by the Free Software Foundation; either version 2.1 of
  * the License, or (at your option) any later version.
  *
  * This software is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  * Lesser General Public License for more details.
  *
  * You should have received a copy of the GNU Lesser General Public
  * License along with this software; if not, write to the Free
  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.encog.neural.networks;

import org.encog.matrix.Matrix;
import org.encog.neural.data.NeuralData;

public interface Layer {
	public NeuralData compute(final NeuralData pattern);

	public void setPrevious(Layer layer);

	public void setNext(Layer layer);

	public NeuralData getFire();

	public int getNeuronCount();

	public boolean isInput();

	public boolean isHidden();

	public Matrix getMatrix();

	public void reset();

	public int getMatrixSize();

	public void setFire(int i, double activationFunction);

	public void setMatrix(Matrix deleteCol);

	public boolean isOutput();

	public Layer getNext();

	public boolean hasMatrix();
	
	public void setFire(NeuralData fire);
}
