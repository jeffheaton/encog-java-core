package org.encog.normalize.segregate;

import org.encog.normalize.Normalization;

public interface Segregator {
	Normalization getNormalization();

	void init(Normalization normalization);

	boolean shouldInclude();
}
