/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util;

import java.io.File;

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

	public void clearContents() {
		for(File innerFile: this.tempdir.listFiles()) {
			innerFile.delete();
		}
	}
	
	
	public void dispose() {
		//recursiveDelete(this.tempdir);
	}
	
	public String toString() {
		return this.tempdir.toString();
	}
}
