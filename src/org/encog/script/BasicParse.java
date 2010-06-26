package org.encog.script;

import java.util.List;

public class BasicParse extends Basic {

	String name;
	BasicParse(BasicModule m)
	{
		
	}
	
	BasicParse()
	{
		
	}

	// Parse Functions
	void advance()
	{
		
	}
	
	char getchr()
	{
		return 0;
	}
	
	void kill_space()
	{
		
	}
	
	void ParseVariable(BasicVariable v,long x,long y)
	{	
	}
	
	boolean ParseVariable(BasicVariable var)
	{
		return false;
	}
	
	void ExpectToken(char ch)
	{	
	}
	
	boolean LookAhead(char ch)
	{	
		return false;
	}
	
	boolean LookAhead(String lookfor,boolean partial)
	{
		return false;
	}
	
	boolean LookAhead(KeyNames token,boolean partial)
	{
		return false;
	}

	BasicKey ParseNextToken()
	{	
		return null;
	}
	
	void Init()
	{	
	}
	
	boolean parse(String l)
	{	
		return false;
	}
	
	void DoAssignment()
	{	
	}
	
	boolean GlobalParse(String l)
	{
		return false;
	}
	
	BasicVariable CreateVariable(String var)
	{
		return null;
	}
	
	boolean CallSub()
	{	
		return false;
	}
	
	void DisplayExpression(long l)
	{	
	}
	
	void DoFileInput(long l)
	{	
	}

	boolean DoError()
	{
		return false;
	}
	
	void SetERR(ErrorNumbers n)
	{	
	}
	
	void Constant(BasicVariable v)
	{	
	}
	
	void Variable(BasicVariable v)
	{	
	}
	
	void Expr(BasicVariable v)
	{	
	}
	
	void Expr1(BasicVariable v)
	{	
	}
	
	void Expr1p5(BasicVariable v)
	{	
	}
	
	void ParseString(String str)
	{	
	}
	
	void ParseVariable(String str)
	{	
	}
	
	void RequestBreak()
	{
		requestBreak=true;
	}
	
	void go(String label)
	{	
	}
	
	void gosub(String label){	
	}
	
	void rtn()
	{	
	}
	
	boolean IsAssignment()
	{
		return false;
	}

	boolean EvaluateDo()
	{	
		return false;
	}

	BasicVariable GetNextVariable(String n)
	{
		return null;
	}
	
	BasicVariable GetVariable(String name)
	{
		return null;
	}

	List variables;// Linked list of the variables, either local or a pointer to globals in program
	BasicModule module;
	BasicLine currentLine;

	boolean Scan(BasicVariable target)
	{
		return false;
	}
	
	boolean Update()
	{
		return false;
	}
	
	boolean Execute()
	{
		return false;
	}
	
	boolean NewObject()
	{
		return false;
	}
	
	BasicVariable CreateObject()
	{
		return null;
	}
	
	void CreateGlobals()
	{
		
	}
	
	void MoveNextLine()
	{	
	}
	
	void MovePastColen()
	{	
	}
	
	void Maint()
	{	
	}
	
	boolean Call(String fn)
	{
		return false;
	}
	
	boolean EventCall(String fn,int x,int y,int z)
	{	
		return false;
	}
	
	boolean Call()
	{
		return false;
	}
	
	void ParamaterList(BasicParse caller)
	{	
	}
	
	void EventParamaterList(int x,int y,int z)
	{	
	}
	
	void CallInternalFunction(KeyNames f,BasicVariable target)
	{	
	}

	void FnAbs(BasicVariable target)
	{	
	}
	
	void FnAsc(BasicVariable target)
	{	
	}
	
	void FnAtn(BasicVariable target)
	{	
	}
	
	void FnCDbl(BasicVariable target)
	{	
	}
	
	void FnChr(BasicVariable target)
	{	
	}
	
	void FnCInt(BasicVariable target)
	{	
	}
	
	void FnCLng(BasicVariable target)
	{	
	}
	
	void FnCos(BasicVariable target)
	{	
	}
	
	void FnDate_(BasicVariable target)
	{	
	}
	
	void FnEnvron_(BasicVariable target)
	{	
	}
	
	void FnEof(BasicVariable target)
	{	
	}
	
	void FnErr(BasicVariable target)
	{	
	}
	
	void FnError(BasicVariable target)
	{	
	}
	
	void FnExp(BasicVariable target)
	{	
	}
	
	void FnFileattr(BasicVariable target)
	{	
	}
	
	void FnFix(BasicVariable target)
	{	
	}
	
	void FnFreeFile(BasicVariable target)
	{	
	}
	
	void FnHex_(BasicVariable target)
	{	
	}
	
	void FnInput_(BasicVariable target)
	{	
	}
	
	void FnInStr(BasicVariable target)
	{	
	}
	
	void FnInt(BasicVariable target)
	{	
	}
	
	void FnLCase(BasicVariable target)
	{	
	}
	
	void FnLeft(BasicVariable target)
	{	
	}
	
	void FnLen(BasicVariable target)
	{	
	}
	
	void FnLoc(BasicVariable target)
	{	
	}
	
	void FnLog(BasicVariable target)
	{	
	}
	
	void FnLTrim(BasicVariable target)
	{	
	}
	
	void FnMid_(BasicVariable target)
	{	
	}
	
	void FnMsgBox(BasicVariable target)
	{	
	}
	
	void FnOct_(BasicVariable target)
	{	
	}
	
	void FnRight_(BasicVariable target)
	{	
	}
	
	void FnRnd(BasicVariable target)
	{	
	}
	
	void FnRTrim(BasicVariable target)
	{	
	}
	
	void FnSeek(BasicVariable target)
	{	
	}
	
	void FnSgn(BasicVariable target)
	{	
	}
	
	void FnShell(BasicVariable target)
	{	
	}
	
	void FnSin(BasicVariable target)
	{	
	}
	
	void FnSpace_(BasicVariable target)
	{	
	}
	
	void FnSpc(BasicVariable target)
	{	
	}
	
	void FnSqr(BasicVariable target)
	{	
	}
	
	void FnStr_(BasicVariable target)
	{	
	}
	
	void FnStrig(BasicVariable target)
	{	
	}
	
	void FnString_(BasicVariable target)
	{	
	}
	
	void FnTan(BasicVariable target)
	{	
	}
	
	void FnTime_(BasicVariable target)
	{	
	}
	
	void FnUCase_(BasicVariable target)
	{	
	}
	
	void FnVal(BasicVariable target)
	{	
	}
	
	void FnRegistry(BasicVariable target)
	{	
	}
	

	void CmdBeep()
	{	
	}
	
	void CmdCall()
	{	
	}
	
	void CmdCase()
	{	
	}
	
	void CmdChDir()
	{	
	}
	
	void CmdClose()
	{	
	}
	
	void CmdCls()
	{	
	}
	
	void CmdConst()
	{	
	}
	
	void CmdCreateLink()
	{	
	}
	
	void CmdDim()
	{	
	}
	
	void CmdDo()
	{	
	}
	
	void CmdElse()
	{	
	}
	
	void CmdElseIf()
	{	
	}
	
	boolean CmdEnd()
	{	
		return false;
	}
	
	boolean CmdEndIf()
	{	
		return false;
	}
	
	void CmdEnviron()
	{	
	}
	
	void CmdErase()
	{	
	}
	
	boolean CmdExit()
	{
		return false;
	}
	
	void CmdFor()
	{	
	}
	
	void CmdFunction()
	{	
	}
	
	void CmdGet()
	{	
	}
	
	void CmdGosub()
	{	
	}
	
	void CmdGoto()
	{	
	}
	
	int  CmdIf()
	{
		return 0;
	}
	
	void CmdInput()
	{	
	}
	
	void CmdKill()
	{	
	}
	
	void CmdLet()
	{	
	}
	
	void CmdLoad()
	{	
	}
	
	void CmdLock()
	{	
	}
	
	void CmdLoop()
	{	
	}
	
	void CmdLSet()
	{	
	}
	
	void CmdMsgBox()
	{	
	}
	
	void CmdMKDir()
	{	
	}
	
	void CmdName()
	{	
	}
	
	void CmdNext()
	{	
	}
	
	boolean CmdOn()
	{	
		return false;
	}
	
	void CmdOnError()
	{	
	}
	
	void CmdOpen()
	{	
	}
	
	void CmdOption()
	{	
	}
	
	void CmdPrint()
	{	
	}
	
	void CmdPut()
	{	
	}
	
	void CmdRandomize()
	{	
	}
	
	void CmdRead()
	{	
	}
	
	void CmdReDim()
	{	
	}
	
	void CmdRem()
	{	
	}
	
	void CmdReset()
	{	
	}
	
	void CmdResume()
	{	
	}
	
	void CmdReturn()
	{	
	}
	
	void CmdRmDir()
	{	
	}
	
	void CmdRSet()
	{	
	}
	
	boolean CmdRun(String str)
	{
		return false;
	}
	
	void CmdSeek()
	{	
	}
	
	void CmdSelect()
	{	
	}
	
	void CmdShared()
	{	
	}
	
	void CmdSleep()
	{	
	}
	
	void CmdStatic()
	{	
	}
	
	void CmdStop()
	{	
	}
	
	void CmdSub()
	{	
	}
	
	void CmdType()
	{	
	}
	
	void CmdUnLock()
	{	
	}
	
	void CmdWEnd()
	{	
	}
	
	void CmdWhile()
	{	
	}
	
	void CmdWidth()
	{	
	}
	
	void CmdWrite()
	{	
	}
	
	void CmdSetRegistry()
	{	
	}
	

	String errorLabel;
	char nextchar;// The next character to be parsed

	boolean LoadLine(String l)
	{
		return false;
	}
	
	void MoveToEndIf(boolean stopOnElse)
	{	
	}
	
	void MoveToNext()
	{	
	}
	
	void MoveToWEnd()
	{	
	}
	
	void MoveToLoop()
	{	
	}
	
	void MoveToEndCase()
	{	
	}
	
	Stack stack;// The stack
	boolean requestBreak;
	String line;
	int ptr;// The line we're parsing and where we're at
	
	long column;
	BasicVariable is;// Current value of IS keyword

	
	
}
