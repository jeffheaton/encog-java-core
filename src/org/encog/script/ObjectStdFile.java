package org.encog.script;

import java.io.File;

public class ObjectStdFile extends BasicObjectVariable {

		ObjectStdFile()
		{
	
		}

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
		void Allocate()
		{
			
		}
		void Free()
		{
			
		}
		void Copy(BasicVariable v)
		{
			
		}
		BasicObjectVariable CreateObject(long num)
		{
			return new ObjectStdFile();
		}
		
		void CreateFilter()
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
