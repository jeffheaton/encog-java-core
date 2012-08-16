package org.encog.app.generate;

public enum TargetLanguage {
	NoGeneration,
	Java,
	JavaScript,
	CSharp,
	MQL4,
	NinjaScript;
	
	public String getExtension() {
		if( this==Java ) {
			return "java";
		} else if( this==JavaScript ) {
			return "html";
		} else if( this==CSharp ) {
			return "cs";
		} else if( this==MQL4 ) {
			return "mql4";
		} else if( this==NinjaScript ) {
			return "cs";
		} else {
			return "txt";
		}
	}
}
