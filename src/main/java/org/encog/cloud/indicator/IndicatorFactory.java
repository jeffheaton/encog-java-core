package org.encog.cloud.indicator;

public interface IndicatorFactory {
	String getName();
	IndicatorListener create();
}
