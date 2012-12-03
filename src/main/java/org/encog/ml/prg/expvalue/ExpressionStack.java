package org.encog.ml.prg.expvalue;

import org.encog.EncogError;

public class ExpressionStack {
	private ExpressionValue[] stack;
	private int position;
	
	public ExpressionStack(int theStackSize) {
		this.stack = new ExpressionValue[theStackSize];
		for(int i=0;i<this.stack.length;i++) {
			this.stack[i] = new ExpressionValue(0);
		}
	}
	
	public boolean isEmpty() {
		return this.position==0;
	}
	
	public ExpressionValue pop() {
		if( isEmpty() ) {
			throw new EncogError("Stack is empty, not enough values to perform that function.");
		}
		this.position--;
		ExpressionValue result = this.stack[this.position];
		return result;
	}
	
	public void push(boolean value) {
		checkOverflow();
		this.stack[this.position++].setValue(value);
	}
	
	public void push(double value) {
		checkOverflow();
		this.stack[this.position++].setValue(value);
	}
	
	public void push(long value) {
		checkOverflow();
		this.stack[this.position++].setValue(value);
	}
	
	public void push(ExpressionValue value) {
		checkOverflow();
		this.stack[this.position++].setValue(value);
	}
	
	public void push(String value) {
		checkOverflow();
		this.stack[this.position++].setValue(value);
	}
	
	private void checkOverflow() {
		if( (this.position+1)>=this.stack.length ) {
			throw new EncogError("Stack overflow.");
		}
	}
	
	public void operationAdd() {
		ExpressionValue b = pop();
		ExpressionValue a = pop();
		
		if( a.isString() || b.isString() ) {
			push(a.toStringValue() + b.toStringValue());
		} else if( a.isInt() && b.isInt() ) {
			push(a.toIntValue() + b.toIntValue());
		} else {
			push(a.toFloatValue() + b.toFloatValue());
		}
	}
	
	public void operationSub() {
		ExpressionValue b = pop();
		ExpressionValue a = pop();
		
		if( a.isInt() && b.isInt() ) {
			push(a.toIntValue() - b.toIntValue());
		}
		else {
			push(a.toFloatValue() - b.toFloatValue());
		}
	}
	
	public void operationMul() {
		ExpressionValue b = pop();
		ExpressionValue a = pop();
		
		if( a.isInt() && b.isInt() ) {
			push(a.toIntValue() * b.toIntValue());
		} else {
			push(a.toFloatValue() * b.toFloatValue());
		}
	}
	
	public void operationDiv() {
		ExpressionValue b = pop();
		ExpressionValue a = pop();
		
		if( a.isInt() && b.isInt() ) {
			push(a.toIntValue() / b.toIntValue());
		} else {
			push(a.toFloatValue() / b.toFloatValue());
		}
	}
	
	public void operationPow() {
		ExpressionValue b = pop();
		ExpressionValue a = pop();
		
		if( a.isInt() && b.isInt() ) {
			push(Math.pow(a.toIntValue(),b.toIntValue()));
		} else {
			push(Math.pow(a.toFloatValue(), b.toFloatValue()));
		}
		
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[ExpressionStack: ");
		
		for(int i=this.position;i>=0;i--) {
			result.append(this.stack[i].toStringValue());
			result.append(" ");
		}
		result.append("]");
		return result.toString();
	}
}
