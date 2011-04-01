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
		try {
			final File sysTempDir = new File(
					System.getProperty("java.io.tmpdir"));

			final int maxAttempts = 9;
			int attemptCount = 0;
			do {
				attemptCount++;
				if (attemptCount > maxAttempts) {
					throw new IOException(
							"The highly improbable has occurred! Failed to "
									+ "create a unique temporary directory after "
									+ maxAttempts + " attempts.");
				}
				String dirName = UUID.randomUUID().toString();
				tempdir = new File(sysTempDir, dirName);
			} while (tempdir.exists());

			if (!tempdir.mkdirs()) {
				throw new IOException("Failed to create temp dir named "
						+ tempdir.getAbsolutePath());
			}
		} catch (IOException e) {
			throw new EncogError(e);
		}
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
	public boolean recursiveDelete(File fileOrDir)
	{
	    if(fileOrDir.isDirectory())
	    {
	        // recursively delete contents
	        for(File innerFile: fileOrDir.listFiles())
	        {
	            if(!recursiveDelete(innerFile))
	            {
	                return false;
	            }
	        }
	    }

	    return fileOrDir.delete();
	}

	
	public void dispose() {
		if(!recursiveDelete(this.tempdir)) {
			throw new EncogError("Failed to delete:" + this.tempdir.toString());
		}

	}
	
	public String toString() {
		return this.tempdir.toString();
	}
}
