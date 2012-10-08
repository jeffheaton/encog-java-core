package org.encog.app.generate.generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.encog.app.analyst.EncogAnalyst;
import org.encog.app.generate.AnalystCodeGenerationError;
import org.encog.util.file.ResourceInputStream;
import org.encog.util.logging.EncogLogging;

public abstract class AbstractTemplateGenerator implements TemplateGenerator {

	private StringBuilder contents = new StringBuilder();
	private EncogAnalyst analyst;
	private int indentLevel = 0;
	
	public abstract String getTemplatePath();
	public abstract void processToken(String command);
	
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

	@Override
	public String getContents() {
		return this.contents.toString();
	}

	@Override
	public void generate(EncogAnalyst theAnalyst) {
		InputStream is = null;
		BufferedReader br = null;
		
		this.analyst = theAnalyst;
		
		try {
			is = ResourceInputStream.openResourceInputStream(getTemplatePath());
			br = new BufferedReader(new InputStreamReader(is));
		
			String line;
			while( (line=br.readLine())!=null ) {
				if( line.startsWith("~~") ) {
					processToken(line.substring(2).trim());
				} else {
					this.contents.append(line);
					this.contents.append("\n");
				}
			}
		br.close();
		} catch(IOException ex) {
			throw new AnalystCodeGenerationError(ex);
		} finally {
			if( is!=null ) {
				try {
					is.close();
				} catch(IOException ex) {
					EncogLogging.log(EncogLogging.LEVEL_ERROR, ex);
				}
			}
			
			if( br!=null ) {
				try {
					br.close();
				} catch(IOException ex) {
					EncogLogging.log(EncogLogging.LEVEL_ERROR, ex);
				}
			}
		}
		
	}
	public EncogAnalyst getAnalyst() {
		return analyst;
	}
	
	public void addLine(String line) {
		for(int i=0;i<this.indentLevel;i++) {
			this.contents.append("\t");
		}
		this.contents.append(line);
		this.contents.append("\n");
	}
	public int getIndentLevel() {
		return indentLevel;
	}
	public void setIndentLevel(int indentLevel) {
		this.indentLevel = indentLevel;
	}
	
	public void indentIn() {
		this.indentLevel++;
	}
	
	public void indentOut() {
		this.indentLevel--;
	}

}
