package org.encog.app.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EncogCodeGeneration {
	
	private final TargetLanguage targetLanguage; 
	private final File targetFile;
	private final StringBuilder contents = new StringBuilder();
	
	public EncogCodeGeneration(TargetLanguage theTargetLanguage, File theTargetFile) {
		this.targetLanguage = theTargetLanguage;
		this.targetFile = theTargetFile;
	}
	
	
	
	/**
	 * @return the targetLanguage
	 */
	public TargetLanguage getTargetLanguage() {
		return targetLanguage;
	}



	/**
	 * @return the targetFile
	 */
	public File getTargetFile() {
		return targetFile;
	}
	
	private void addComment(String comment) {
		this.contents.append("// " + comment);
		addBreak();
	}
	
	private void addBreak() {
		this.contents.append("\n");
	}

	public void generate() {
		addComment("Hello World");
		writeContents();
	}


	public void writeContents() {
		try {
			FileWriter outFile = new FileWriter(this.targetFile);
			PrintWriter out = new PrintWriter(outFile);
			out.print(this.contents.toString());
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
