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
	
	public abstract String getTemplatePath();
	
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
	public void generate(EncogAnalyst analyst) {
		InputStream is = null;
		BufferedReader br = null;
		
		try {
			is = ResourceInputStream.openResourceInputStream(getTemplatePath());
			br = new BufferedReader(new InputStreamReader(is));
		
			String line;
			while( (line=br.readLine())!=null ) {
				this.contents.append(line);
				this.contents.append("\n");
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

}
