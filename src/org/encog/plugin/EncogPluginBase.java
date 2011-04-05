package org.encog.plugin;

public interface EncogPluginBase {
	int getPluginType();
	int getPluginFrameworkVersion();
	int getPluginVersion();
	String getPluginName();
	String getPluginDescription();
	Object[] execute(String command,Object[] args);
}
