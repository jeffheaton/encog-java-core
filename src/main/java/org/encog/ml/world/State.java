package org.encog.ml.world;

public interface State {
	void setProperty(String key, Object value);
	Object getProperty(String key);
}
