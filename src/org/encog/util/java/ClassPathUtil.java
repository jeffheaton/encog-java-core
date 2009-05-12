package org.encog.util.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ClassPathUtil {

	public static String generateClassPath(final char sep, final File jarDir) {
		final StringBuilder builder = new StringBuilder();
		final File[] contents = jarDir.listFiles();
		for (final File entry : contents) {
			if (entry.isFile()) {
				String str = entry.toString();
				final int idx = str.indexOf("jar");

				if (idx != -1) {
					str = str.substring(idx);
				}
				if (builder.length() > 0) {
					builder.append(sep);
				}
				builder.append(str);
			}
		}

		return builder.toString();
	}

	public static void main(final String args[]) {
		if (args.length != 1) {
			System.out.println("Please pass the workbench directory as arg 1");
		} else {
			try {
				// generate the directories
				final File baseDir = new File(args[0]);
				final File jarDir = new File(baseDir, "jar");

				// build the batch command
				final StringBuilder batCommand = new StringBuilder();
				batCommand.append("start javaw -classpath ");
				batCommand.append(ClassPathUtil.generateClassPath(';', jarDir));
				batCommand.append(" org.encog.workbench.EncogWorkBench");

				PrintWriter out = new PrintWriter(new BufferedWriter(
						new FileWriter(new File(baseDir, "workbench.bat"))));
				out.println(batCommand);
				out.close();

				// build the shell command
				final StringBuilder shCommand = new StringBuilder();
				shCommand.append("java -classpath ");
				shCommand.append(ClassPathUtil.generateClassPath(':', jarDir));
				shCommand.append(" org.encog.workbench.EncogWorkBench");

				out = new PrintWriter(new BufferedWriter(new FileWriter(
						new File(baseDir, "workbench.sh"))));
				out.println(shCommand);
				out.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}
}
