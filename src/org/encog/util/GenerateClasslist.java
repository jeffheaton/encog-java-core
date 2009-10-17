package org.encog.util;

import java.io.File;

public class GenerateClasslist {
	
	private String base;
	
	public void beginScan(File dir)
	{
		base = dir.getAbsolutePath();
		scan(dir);
	}
	
	private void processJAVA(File file)
	{
		String name = file.getName();
		
		// strip off the .java
		int idx = name.indexOf('.');
		name = name.substring(0,idx);
		
		// strip the name for the directory
		String path = file.getAbsolutePath();
		idx = path.lastIndexOf(File.separatorChar);
		path = path.substring(0,idx);
		
		// strip off base
		path = path.substring(base.length()+1);
		
		// insert .'s
		StringBuilder temp = new StringBuilder(path);
		for(int i=0;i<temp.length();i++)
		{
			if( temp.charAt(i)==File.separatorChar)
			{
				temp.setCharAt(i, '.');
			}
		}
		
		System.out.println(temp+"."+name);
		
	}
	
	public void scan(File dir)
	{
		File[] files = dir.listFiles();
		for(File file: files)
		{
			if( file.isFile())
			{
				String strFile = file.toString();
				if( strFile.endsWith(".java"))
					processJAVA(file);
			}
			else if( file.isDirectory())
			{
				scan(file);
			}
		}
	}
	
	public static void main(String args[])
	{
		GenerateClasslist gen = new GenerateClasslist();
		gen.beginScan(new File("C:\\shared\\encog-workspace\\encog-core\\src"));
	}
}
