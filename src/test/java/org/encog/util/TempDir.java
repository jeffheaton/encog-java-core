package org.encog.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.encog.EncogError;

/**
 * Based on code from:
 * http://stackoverflow.com/questions/617414/create-a-temporary
 * -directory-in-java
 * 
 */
public class TempDir {

	private File tempdir;

	public TempDir() {
		this.tempdir = new File(System.getProperty("java.io.tmpdir"),"encog-ut");
		this.tempdir.mkdir();
	}
	
	public File createFile(String filename) {
		return new File(this.tempdir,filename);
	}
	
	/**
	 * Recursively delete file or directory
	 * @param fileOrDir
	 *          the file or dir to delete
	 * @return
	 *          true iff all files are successfully deleted
	 */
	public void recursiveDelete(File fileOrDir)
	{
	    if(fileOrDir.isDirectory())
	    {
	        // recursively delete contents
	        for(File innerFile: fileOrDir.listFiles())
	        {
	            recursiveDelete(innerFile);
	        }
	    }

	    fileOrDir.delete();
	}

	
	public void dispose() {
		//recursiveDelete(this.tempdir);
	}
	
	public String toString() {
		return this.tempdir.toString();
	}
}
