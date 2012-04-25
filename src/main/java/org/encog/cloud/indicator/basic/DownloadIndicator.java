package org.encog.cloud.indicator.basic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.encog.EncogError;
import org.encog.cloud.indicator.server.IndicatorLink;
import org.encog.cloud.indicator.server.IndicatorPacket;
import org.encog.cloud.indicator.server.IndicatorServer;
import org.encog.util.logging.EncogLogging;

public class DownloadIndicator extends BasicIndicator {
	public static final int PORT = 5128;
	
	private int rowsDownloaded;
	private Map<String, InstrumentHolder> data = new HashMap<String, InstrumentHolder>();	
	private IndicatorLink mylink;
	private IndicatorServer node;
	private boolean done;
	private File targetFile;
	
	public DownloadIndicator(File theFile) 
	{		
		this.targetFile = theFile;
	}

	@Override
	public void notifyPacket(IndicatorPacket packet) {
		if (packet.getCommand().equalsIgnoreCase("bar")) {
			try {
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
			} catch (Exception ex) {
				EncogLogging.log(ex);
			}

		}		
	}
	
	@Override
	public void notifyTermination()
	{
		try {
			FileWriter outFile = new FileWriter(targetFile);
			PrintWriter out = new PrintWriter(outFile);

			out.print("\"INSTRUMENT\",\"WHEN\"");
			for( String str: this.getDataRequested() ) {
				out.print(",\""+str+"\"");
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
