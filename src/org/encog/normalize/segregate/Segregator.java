package org.encog.normalize.segregate;

import org.encog.normalize.Normalization;

public interface Segregator {
	void init(Normalization normalization);
	boolean shouldInclude();
	Normalization getNormalization();
}
