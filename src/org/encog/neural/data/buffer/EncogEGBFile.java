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
			ByteBuffer bb = ByteBuffer.allocate(EncogEGBFile.HEADER_SIZE);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			bb.put((byte) 'E');
			bb.put((byte) 'N');
			bb.put((byte) 'C');
			bb.put((byte) 'O');
			bb.put((byte) 'G');
			bb.put((byte) '-');
			bb.put((byte) '0');
			bb.put((byte) '0');

			bb.putDouble(input.length);
			bb.putDouble(ideal.length);

			bb.flip();
			fc.write(bb);
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}
	}

	public void open() {
		try {
			this.raf = new RandomAccessFile(this.file, "rw");
			FileChannel fc = raf.getChannel();

			ByteBuffer bb = ByteBuffer.allocate(EncogEGBFile.HEADER_SIZE);
			bb.order(ByteOrder.LITTLE_ENDIAN);

			boolean isEncogFile = true;

			fc.read(bb);
			bb.position(0);

			isEncogFile = isEncogFile ? bb.get() == 'E' : false;
			isEncogFile = isEncogFile ? bb.get() == 'N' : false;
			isEncogFile = isEncogFile ? bb.get() == 'C' : false;
			isEncogFile = isEncogFile ? bb.get() == 'O' : false;
			isEncogFile = isEncogFile ? bb.get() == 'G' : false;
			isEncogFile = isEncogFile ? bb.get() == '-' : false;

			if (!isEncogFile)
				throw new BufferedDataError(
						"File is not a valid Encog binary file:"
								+ this.file.toString());

			char v1 = (char) bb.get();
			char v2 = (char) bb.get();
			String versionStr = "" + v1 + v2;

			try {
				int version = Integer.parseInt(versionStr);
				if (version > 0)
					throw new BufferedDataError(
							"File is from a newer version of Encog than is currently in use.");
			} catch (NumberFormatException ex) {
				throw new BufferedDataError("File has invalid version number.");
			}

			this.inputCount = (int) bb.getDouble();
			this.idealCount = (int) bb.getDouble();

			this.recordCount = inputCount + idealCount;
			this.recordSize = recordCount * EncogEGBFile.DOUBLE_SIZE;

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
			for (int i = 0; i < recordCount; i++) {
				d[i] = this.recordBuffer.getDouble();
			}
		} catch (IOException ex) {
			throw new BufferedDataError(ex);
		}

	}
}
