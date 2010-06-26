package org.encog.script;

public abstract class Basic extends BasicObject {

	abstract boolean Scan(BasicVariable target);
	abstract boolean Update();
	abstract boolean Execute();
	abstract boolean NewObject();
	abstract BasicVariable CreateObject();
	abstract void CreateGlobals();
	abstract void Maint();
	private long ifs;
	
}
