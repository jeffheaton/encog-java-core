package org.encog.ml.world;

public interface State {
	void setProperty(String key, Object value);
	Object getProperty(String key);
	double getReward();
	void setReward(double r);
	double []getPolicyValue();
	void setAllPolicyValues(double d);
	void setPolicyValueSize(int s);
	boolean wasVisited();
	void setVisited(boolean b);
}
