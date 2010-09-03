package org.encog.neural.data.buffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class EncogEGBFile {

	/**
	 * The size of a double.
	 */
	public final static int DOUBLE_SIZE = Double.SIZE / 8;

	public final static int HEADER_SIZE = DOUBLE_SIZE * 3;

	private File file;
	private int inputCount;
	private int idealCount;

	private RandomAccessFile raf;
	private FileChannel fc;
	private ByteBuffer headerBuffer;
	private ByteBuffer recordBuffer;
	private int recordCount;
	private int recordSize;
	private int numberOfRecords;

	public EncogEGBFile(File file) {
		this.file = file;
		this.headerBuffer = ByteBuffer.allocate(HEADER_SIZE);
	}

	public void create(int inputCount, int idealCount) {
		try {
			this.inputCount = inputCount;
			this.idealCount = idealCount;

			double[] input = new double[inputCount];
			double[] ideal = new double[idealCount];

			this.file.delete();
			this.raf = new RandomAccessFile(this.file, "rw");
			this.fc = raf.getChannel();
			
			this.headerBuffer.clear();
			this.headerBuffer.order(ByteOrder.LITTLE_ENDIAN);

			this.headerBuffer.put((byte) 'E');
			this.headerBuffer.put((byte) 'N');
			this.headerBuffer.put((byte) 'C');
			this.headerBuffer.put((byte) 'O');
			this.headerBuffer.put((byte) 'G');
			this.headerBuffer.put((byte) '-');
			this.headerBuffer.put((byte) '0');
			this.headerBuffer.put((byte) '0');

			this.headerBuffer.putDouble(input.length);
			this.headerBuffer.putDouble(ideal.length);
			
			this.numberOfRecords = 0;
			this.recordCount = this.inputCount+this.idealCount;
			this.recordSize = this.recordCount*EncogEGBFile.DOUBLE_SIZE;
			this.recordBuffer = ByteBuffer.allocate(this.recordSize);
			
			this.headerBuffer.flip();
			fc.write(this.headerBuffer);
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	public void open() {
		try {
			this.raf = new RandomAccessFile(this.file, "rw");
			this.fc = raf.getChannel();

			this.headerBuffer.clear();
			this.headerBuffer.order(ByteOrder.LITTLE_ENDIAN);

			boolean isEncogFile = true;

			fc.read(this.headerBuffer);
			this.headerBuffer.position(0);

			isEncogFile = isEncogFile ? this.headerBuffer.get() == 'E' : false;
			isEncogFile = isEncogFile ? this.headerBuffer.get() == 'N' : false;
			isEncogFile = isEncogFile ? this.headerBuffer.get() == 'C' : false;
			isEncogFile = isEncogFile ? this.headerBuffer.get() == 'O' : false;
			isEncogFile = isEncogFile ? this.headerBuffer.get() == 'G' : false;
			isEncogFile = isEncogFile ? this.headerBuffer.get() == '-' : false;

			if (!isEncogFile)
				throw new BufferedDataError(
						"File is not a valid Encog binary file:"
								+ this.file.toString());

			char v1 = (char) this.headerBuffer.get();
			char v2 = (char) this.headerBuffer.get();
			String versionStr = "" + v1 + v2;

			try {
				int version = Integer.parseInt(versionStr);
				if (version > 0)
					throw new BufferedDataError(
							"File is from a newer version of Encog than is currently in use.");
			} catch (NumberFormatException ex) {
				throw new BufferedDataError("File has invalid version number.");
			}

			this.inputCount = (int) this.headerBuffer.getDouble();
			this.idealCount = (int) this.headerBuffer.getDouble();
			
			this.recordCount = this.inputCount+this.idealCount;
			this.recordSize = this.recordCount*EncogEGBFile.DOUBLE_SIZE;
			this.numberOfRecords = (int)(this.file.length()/this.recordSize);

			this.recordBuffer = ByteBuffer.allocate(this.recordSize);			
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}

	}

	public void close() {
		try {
			if (this.raf != null) {
				this.raf.close();
				this.raf = null;
			}
			if (this.fc != null) {
				this.fc.close();
				this.fc = null;
			}
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	private int calculateIndex(int row) {
		return EncogEGBFile.HEADER_SIZE + (row * this.recordSize);
	}

	private int calculateIndex(int row, int col) {
		return EncogEGBFile.HEADER_SIZE + (row * this.recordSize)
				+ (col * EncogEGBFile.DOUBLE_SIZE);
	}

	public void setLocation(int row) {
		try {
			this.fc.position(calculateIndex(row));
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	private void clear() {
		this.recordBuffer.clear();
		this.recordBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}

	public void write(int row, int col, double v) {
		try {
			clear();
			this.recordBuffer.putDouble(v);
			this.recordBuffer.flip();
			this.fc.write(this.recordBuffer, calculateIndex(row, col));
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	public void write(int row, double[] v) {
		try {
			clear();
			for (int i = 0; i < v.length; i++) {
				this.recordBuffer.putDouble(v[i]);
			}
			this.recordBuffer.flip();
			this.fc.write(this.recordBuffer);
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}

	}

	public void write(double[] v) {
		try {
			clear();
			for (int i = 0; i < v.length; i++) {
				this.recordBuffer.putDouble(v[i]);
			}
			this.recordBuffer.flip();
			this.fc.write(this.recordBuffer);
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}
	
	public void write(byte b) {
		try {
			clear();
			this.recordBuffer.put(b);
			this.recordBuffer.flip();
			this.fc.write(this.recordBuffer);
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}		
	}	

	public double read(int row, int col) {
		try {
			clear();
			this.fc.read(this.recordBuffer, calculateIndex(row, col));
			this.recordBuffer.position(0);
			return this.recordBuffer.getDouble(0);
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}

	}

	public void read(int row, double[] d) {
		try {
			clear();
			this.fc.read(this.recordBuffer, calculateIndex(row));
			this.recordBuffer.position(0);
			for (int i = 0; i < recordCount; i++) {
				d[i] = this.recordBuffer.getDouble();
			}
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	public void read(double[] d) {
		try {
			clear();
			this.fc.read(this.recordBuffer);
			this.recordBuffer.position(0);
			for (int i = 0; i < d.length; i++) {
				d[i] = this.recordBuffer.getDouble();
			}
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}
	
	public double read() {
		try {
			clear();
			this.recordBuffer.limit(EncogEGBFile.DOUBLE_SIZE);
			this.fc.read(this.recordBuffer);
			this.recordBuffer.position(0);			
			return this.recordBuffer.getDouble();
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the inputCount
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * @return the idealCount
	 */
	public int getIdealCount() {
		return idealCount;
	}

	/**
	 * @return the raf
	 */
	public RandomAccessFile getRaf() {
		return raf;
	}

	/**
	 * @return the fc
	 */
	public FileChannel getFc() {
		return fc;
	}

	/**
	 * @return the headerBuffer
	 */
	public ByteBuffer getHeaderBuffer() {
		return headerBuffer;
	}

	/**
	 * @return the recordBuffer
	 */
	public ByteBuffer getRecordBuffer() {
		return recordBuffer;
	}

	/**
	 * @return the recordCount
	 */
	public int getRecordCount() {
		return recordCount;
	}

	/**
	 * @return the recordSize
	 */
	public int getRecordSize() {
		return recordSize;
	}

	/**
	 * @return the numberOfRecords
	 */
	public int getNumberOfRecords() {
		return numberOfRecords;
	}

}
