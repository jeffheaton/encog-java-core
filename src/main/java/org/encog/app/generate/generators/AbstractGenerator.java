package org.encog.app.generate.generators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

import org.encog.app.analyst.EncogAnalyst;

public abstract class AbstractGenerator implements ProgramGenerator {
	public static final int INDENT_SPACES = 4;
	
	private final StringBuilder contents = new StringBuilder();
	private int currentIndent = 0;
	private final Set<String> includes = new TreeSet<String>();

	public void addLine(String line) {
		for(int i=0;i<currentIndent;i++) {
			this.contents.append(' ');
		}
		this.contents.append(line);
		this.contents.append("\n");
	}
	
	public void indentLine(String line) {		
		addLine(line);
		this.currentIndent+=INDENT_SPACES;
	}
	
	public void unIndentLine(String line) {
		this.currentIndent-=INDENT_SPACES;
		addLine(line);
	}
	
	public void addBreak() {
		this.contents.append("\n");
	}

	public String getContents() {
		return contents.toString();
	}
	
	public void addInclude(String str) {
		this.includes.add(str);
	}
	
	public Set<String> getIncludes() {
		return includes;
	}

	public void writeContents(File targetFile) {
		try {
			FileWriter outFile = new FileWriter(targetFile);
			PrintWriter out = new PrintWriter(outFile);
			out.print(this.contents.toString());
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void addToBeginning(String str) {
		this.contents.insert(0, str);
	}
	
	
}
