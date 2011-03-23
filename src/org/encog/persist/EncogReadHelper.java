package org.encog.persist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EncogReadHelper {

	private BufferedReader reader;

	private List<String> lines = new ArrayList<String>();
	private String currentSectionName = "";
	private String currentSubSectionName = "";
	private EncogFileSection section;

	public EncogReadHelper(InputStream is) {
		this.reader = new BufferedReader(new InputStreamReader(is));
	}

	public EncogFileSection readNextSection() {

		try {
			String line;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				// is it a comment
				if (line.startsWith("//")) {
					continue;
				}

				// is it a section or subsection
				else if (line.startsWith("[")) {
					// handle previous section
					this.section = new EncogFileSection(currentSectionName,
							currentSubSectionName);
					this.section.getLines().addAll(this.lines);

					// now begin the new section
					lines.clear();
					String s = line.substring(1).trim();
					if (!s.endsWith("]"))
						throw new PersistError("Invalid section: " + line);
					s = s.substring(0, line.length() - 2);
					int idx = s.indexOf(':');
					if (idx == -1) {
						currentSectionName = s;
						currentSubSectionName = "";
					} else {
						if (currentSectionName.length() < 1) {
							throw new PersistError(
									"Can't begin subsection when a section has not yet been defined: "
											+ line);
						}

						String newSection = s.substring(0, idx);
						String newSubSection = s.substring(idx + 1);

						if (!newSection.equals(currentSectionName)) {
							throw new PersistError("Can't begin subsection "
									+ line
									+ ", while we are still in the section: "
									+ currentSectionName);
						}

						currentSubSectionName = newSubSection;
					}
					return this.section;
				} else if (line.length() < 1) {
					continue;
				} else {
					if (currentSectionName.length() < 1) {
						throw new PersistError(
								"Unknown command before first section: " + line);
					}

					lines.add(line);
				}

			}
			this.section = new EncogFileSection(currentSectionName,currentSubSectionName);
			this.section.getLines().addAll(this.lines);
			return this.section;
		} catch (IOException ex) {
			throw new PersistError(ex);
		}

	}

	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			throw new PersistError(e);
		}
	}
}
