package org.encog.normalize.segregate.index;

import org.encog.normalize.Normalization;
import org.encog.normalize.segregate.Segregator;

public abstract class IndexSegregator implements Segregator {
	
	private int currentIndex =  0;
	private Normalization normalization;
	
	public void rollIndex()
	{
		this.currentIndex++;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}
	
	public Normalization getNormalization() {
		return this.normalization;
	}


	public void init(Normalization normalization) {
		this.normalization = normalization;
	}
	
}
