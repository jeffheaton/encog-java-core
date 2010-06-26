package org.encog.script;

public class Stack {

	Stack()
	{
		Clear();
	}
	
	void Clear()
	{
		location=0;
	}
	
	//BasicLine pop(int *off,int *start=NULL,int *stop=NULL,int *step=NULL,char *var=NULL);
	//void push(enumStackEntryType t,int off,BASIC_LINE *v,int start=0,int stop=0,int step=0,char *var=NULL);
	//BASIC_LINE *peek(int *off,int *start=NULL,int *stop=NULL,int *step=NULL,char *var=NULL);
	//int empty(void){return( !location );};
	//enumStackEntryType PeekType(void){if(empty())return stackError;return typeStack[location];};
	
	public StackEntryType getNextType()
	{
		return location>0?typeStack[location-1]:StackEntryType.stackError;
	}

	private int location;
	private BasicLine stack[];
	private int stack2[];
	private int startStack[];
	private int stopStack[];
	private int stepStack[];
	private String varStack[];
	private StackEntryType typeStack[];
	
}
