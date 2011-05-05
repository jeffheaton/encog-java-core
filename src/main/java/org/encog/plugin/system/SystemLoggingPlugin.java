package org.encog.plugin.system;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.plugin.EncogPluginType1;
import org.encog.util.logging.EncogLogging;

public class SystemLoggingPlugin implements EncogPluginType1 {

	/**
	 * The current level.
	 */
	private int currentLevel = EncogLogging.LEVEL_DISABLE;

	private boolean logConsole = false;

	/**
	 * Not used for this type of plugin.
	 * 
	 * @param gradients
	 *            Not used.
	 * @param layerOutput
	 *            Not used.
	 * @param weights
	 *            Not used.
	 * @param layerDelta
	 *            Not used.
	 * @param af
	 *            Not used.
	 * @param index
	 *            Not used.
	 * @param fromLayerIndex
	 *            Not used.
	 * @param fromLayerSize
	 *            Not used.
	 * @param toLayerIndex
	 *            Not used.
	 * @param toLayerSize
	 *            Not used.
	 */
	@Override
	public void calculateGradient(final double[] gradients,
			final double[] layerOutput, final double[] weights,
			final double[] layerDelta, final ActivationFunction af,
			final int index, final int fromLayerIndex, final int fromLayerSize,
			final int toLayerIndex, final int toLayerSize) {

	}

	/**
	 * Not used for this type of plugin.
	 * 
	 * @param weights
	 *            Not used.
	 * @param layerOutput
	 *            Not used.
	 * @param startIndex
	 *            Not used.
	 * @param outputIndex
	 *            Not used.
	 * @param outputSize
	 *            Not used.
	 * @param inputIndex
	 *            Not used.
	 * @param inputSize
	 *            Not used.
	 * @return Not used.
	 */
	@Override
	public final int calculateLayer(final double[] weights,
			final double[] layerOutput, final int startIndex,
			final int outputIndex, final int outputSize, final int inputIndex,
			final int inputSize) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLogLevel() {
		return this.currentLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginDescription() {
		return "This is the built in logging for Encog, it logs "
				+ "to either a file or System.out";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getPluginName() {
		return "HRI-System-Logging";
	}

	/**
	 * @return Returns the service type for this plugin. This plugin provides
	 *         the system calculation for layers and gradients. Therefore, this
	 *         plugin returns SERVICE_TYPE_CALCULATION.
	 */
	@Override
	public final int getPluginServiceType() {
		return EncogPluginType1.SERVICE_TYPE_LOGGING;
	}

	/**
	 * @return This is a type-1 plugin.
	 */
	@Override
	public final int getPluginType() {
		return 1;
	}

	@Override
	public void log(final int level, final String message) {

		if (this.currentLevel < level) {
			Date now = new Date();
			StringBuilder line = new StringBuilder();
			line.append(now.toString());
			line.append(" [");
			switch (this.currentLevel) {
			case EncogLogging.LEVEL_CRITICAL:
				line.append("CRITICAL");
				break;
			case EncogLogging.LEVEL_ERROR:
				line.append("ERROR");
				break;
			case EncogLogging.LEVEL_INFO:
				line.append("INFO");
				break;
			case EncogLogging.LEVEL_DEBUG:
				line.append("DEBUG");
				break;
			}
			line.append("][");
			line.append(Thread.currentThread().getName());
			line.append("]: ");
			line.append(message);

			if (this.logConsole) {
				if (this.currentLevel > EncogLogging.LEVEL_ERROR) {
					System.err.println(line.toString());
				} else {
					System.out.println(line.toString());

				}
			}
		}
	}
	
	  public static String getStackTrace(Throwable aThrowable) {
		    final Writer result = new StringWriter();
		    final PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
		  }


	@Override
	public void log(final int level, final Throwable t) {
		log(level,getStackTrace(t));
	}

	public void setLogLevel(final int level) {
		this.currentLevel = level;
	}
	
	public void stopLogging() {
		
	}
	
	public void startConsoleLogging() {
		stopLogging();
		this.logConsole = true;
		setLogLevel(EncogLogging.LEVEL_DEBUG);
	}

}
