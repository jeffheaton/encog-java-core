package org.encog.script.basic;

public interface Basic  {

	boolean scan(BasicVariable target);
	boolean update();
	boolean execute();
	boolean newObject();
	BasicVariable createObject();
	void createGlobals();
	void maint();	
}
