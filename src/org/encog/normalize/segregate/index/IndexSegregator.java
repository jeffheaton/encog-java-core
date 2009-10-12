package org.encog.normalize.segregate.index;

import org.encog.normalize.Normalization;
import org.encog.normalize.segregate.Segregator;

public abstract class IndexSegregator implements Segregator {

	private int currentIndex = 0;
	private Normalization normalization;

	public int getCurrentIndex() {
		return this.currentIndex;
	}

	public Normalization getNormalization() {
		return this.normalization;
	}

	public void init(final Normalization normalization) {
		this.normalization = normalization;
	}

	public void rollIndex() {
		this.currentIndex++;
	}

}
