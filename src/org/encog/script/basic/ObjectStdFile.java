package org.encog.script.basic;

import java.io.File;


public class ObjectStdFile extends BasicObjectVariable {

		ObjectStdFile()
		{
	
		}

		boolean scan(BasicVariable target)
		{
			return false;
		}
		boolean update()
		{
			return false;
		}
		boolean execute()
		{
			return false;
		}
		void allocate()
		{
			
		}
		void free()
		{
			
		}
		void copy(BasicVariable v)
		{
			
		}
		BasicObjectVariable createObject(long num)
		{
			return new ObjectStdFile();
		}
		
		void createFilter()
		{
			
		}
		File opn;

		String strFilter; 
		String strCustomFilter;
		String strFile;
		String strFileTitle;
		String strInitialDir;
		String strTitle;
		String strDefExt;

	
}
