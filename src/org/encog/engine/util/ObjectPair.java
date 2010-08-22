package org.encog.engine.util;

public class ObjectPair<A,B> {
	
	private final A a;
	private final B b;
	
	public ObjectPair(A a, B b) {
		super();
		this.a = a;
		this.b = b;
	}

	public A getA() {
		return a;
	}

	public B getB() {
		return b;
	}

	
}
