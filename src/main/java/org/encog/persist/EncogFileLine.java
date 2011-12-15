package org.encog.persist;

import java.io.BufferedReader;
import java.io.IOException;

import org.encog.EncogError;
import org.encog.util.Format;

public class EncogFileLine {
	private String line;
	private String name;
	private double[] data;
	
	public EncogFileLine(String theName, String theLine) {
		this.line = theLine.trim();
		if( theName!=null)
		this.name = theName.trim();
	}
	
	public String toString() {
		return this.line;
	}
	
	private static EncogFileLine readDoubleArray(String name, StringBuilder line, BufferedReader reader) {		
		throw new EncogError("Line is too long, over a megabyte.");
	}
	
	public static EncogFileLine read(BufferedReader reader) throws IOException {
		int ch;
		String name = null;
		StringBuilder line = new StringBuilder();
		
		while( (ch=reader.read())!=-1 ) {
			if( ch=='=' && line.length()>0 && line.length()<80 ) {
				name = line.toString().trim();
				line.setLength(0);
			} else if( ch=='\n' || ch=='\r') {
				if( line.length()>0 || name!=null ) 
					break;
			} else {
				if( line.length()>Format.MEMORY_MEG) {
					return readDoubleArray(name,line,reader);
				}
					
				line.append((char)ch);
			}
		}
		
		if( ch==-1 && line.length()==0 ) {
			return null;
		}
		
		return new EncogFileLine(name, line.toString());
	}

	public boolean isProperty() {
		return this.name!=null;
	}

	public String getName() {
		return this.name;
	}
	
	public double[] getData() {
		return this.data;
	}

	public boolean isEmpty() {
		return this.name==null && this.line.length()==0;
	}
}
