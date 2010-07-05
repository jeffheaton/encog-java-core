package org.encog.script.basic;

public interface Basic  {

	boolean Scan(BasicVariable target);
	boolean Update();
	boolean Execute();
	boolean NewObject();
	BasicVariable CreateObject();
	void CreateGlobals();
	void Maint();	
}
