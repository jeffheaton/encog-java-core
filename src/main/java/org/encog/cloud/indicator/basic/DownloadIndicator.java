/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.cloud.indicator.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.cloud.indicator.server.IndicatorPacket;
import org.encog.util.logging.EncogLogging;

/**
 * This indicator is used to download data from an external source.  For example
 * you may want to export financial data from Ninja Trader, including Ninja Trader
 * native indicators.  This class will download that data and write it to a CSV file.
 */
public class DownloadIndicator extends BasicIndicator {

	/**
	 * The default port.
	 */
	public static final int PORT = 5128;

	/**
	 * The number of rows downloaded.
	 */
	private int rowsDownloaded;

	/**
	 * The instruments that we are downloading (i.e. ticker symbols, and their data)
	 */
	private Map<String, InstrumentHolder> data = new HashMap<String, InstrumentHolder>();

	/**
	 * The target CSV file.
	 */
	private File targetFile;

	/**
	 * Construct the download indicator.
	 * @param theFile The local CSV file.
	 */
	public DownloadIndicator(File theFile) {
		super(false);
		this.targetFile = theFile;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyPacket(IndicatorPacket packet) {
		if (packet.getCommand().equalsIgnoreCase("bar")) {
			String security = packet.getArgs()[1];
			long when = Long.parseLong(packet.getArgs()[0]);
			String key = security.toLowerCase();
			InstrumentHolder holder = null;

			if (this.data.containsKey(key)) {
				holder = this.data.get(key);
			} else {
				holder = new InstrumentHolder();
				this.data.put(key, holder);
			}

			if (holder.record(when, 2, packet.getArgs())) {
				this.rowsDownloaded++;
				System.out.println("Received row " + this.rowsDownloaded);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void notifyTermination() {
		try {
			FileWriter outFile = new FileWriter(targetFile);
			PrintWriter out = new PrintWriter(outFile);

			out.print("\"INSTRUMENT\",\"WHEN\"");
			for (String str : this.getDataRequested()) {
				out.print(",\"" + str + "\"");
			}
			out.println();

			for (String ins : this.data.keySet()) {
				InstrumentHolder holder = this.data.get(ins);
				for (Long key : holder.getSorted()) {
					String str = holder.getData().get(key);
					out.println("\"" + ins + "\"," + key + "," + str);
				}
			}

			out.close();
		} catch (IOException ex) {
			throw new EncogError(ex);
		}
	}
}
