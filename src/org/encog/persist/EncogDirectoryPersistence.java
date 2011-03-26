package org.encog.persist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.StringTokenizer;

import org.encog.Encog;

public class EncogDirectoryPersistence {

	private File parent;

	public EncogDirectoryPersistence(File parent) {
		this.parent = parent;
	}

	public static void saveObject(File filename, Object obj) {
		FileOutputStream fos = null;
		
		try {
			fos = new FileOutputStream(filename);
			saveObject(fos, obj);
		} catch (IOException ex) {
			throw new PersistError(ex);
		} finally {
			try {
				fos.close();
			} catch (IOException e) {

			}
		}
	}

	public static Object loadObject(File file) {
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
			Object result = loadObject(fis);
			
			return result;
		} catch (IOException ex) {
			throw new PersistError(ex);
		} finally {
			if( fis!=null ) {
				try {
					fis.close();
				} catch (IOException e) {
					
				}
			}
		}
	}

	public static void saveObject(OutputStream os, Object obj) {
		try {
			EncogPersistor p = PersistorRegistry.getInstance().getPersistor(
					obj.getClass());

			if (p == null) {
				throw new PersistError("Do not know how to persist object: "
						+ obj.getClass().getSimpleName());
			}

			os.flush();
			PrintWriter pw = new PrintWriter(os);
			Date now = new Date();
			pw.println("encog," + obj.getClass().getSimpleName() + ",java,"
					+ Encog.VERSION + "," + p.getFileVersion() + ","
					+ now.getTime());
			pw.flush();
			p.save(os, obj);
		} catch (IOException ex) {
			throw new PersistError(ex);
		}
	}

	private static String readLine(InputStream is) {
		try {
			StringBuilder result = new StringBuilder();

			char ch;

			do {
				int b = is.read();
				if (b == -1)
					return result.toString();

				ch = (char) b;

				if (ch != 13 && ch != 10)
					result.append((char) ch);

			} while (ch != 10);

			return result.toString();
		} catch (IOException ex) {
			throw new PersistError(ex);
		}
	}

	public static Object loadObject(InputStream is) {

		String header = readLine(is);
		String[] params = header.split(",");

		if (!"encog".equals(params[0])) {
			throw new PersistError("Not a valid EG file.");
		}

		String name = params[1];

		EncogPersistor p = PersistorRegistry.getInstance().getPersistor(name);

		if (p == null) {
			throw new PersistError("Do not know how to read the object: "
					+ name);
		}

		if (p.getFileVersion() < Integer.parseInt(params[4])) {
			throw new PersistError(
					"The file you are trying to read is from a later version of Encog.  Please upgrade Encog to read this file.");
		}

		return p.read(is);

	}

	public void saveToDirectory(String name, Object obj) {
		File path = new File(this.parent,name);
		EncogDirectoryPersistence.saveObject(path,obj);
	}

	public Object loadFromDirectory(String name) {
		File path = new File(this.parent,name);
		return EncogDirectoryPersistence.loadObject(path);
	}

	public File getParent() {
		return this.parent;
	}

	public String getEncogType(String name) {
		try {
			File path = new File(this.parent, name);
			BufferedReader br = new BufferedReader(new FileReader(path));
			String header = br.readLine();
			String[] params = header.split(",");
			br.close();
			return params[1];
		} catch (IOException ex) {
			throw new PersistError(ex);
		}
	}

}
