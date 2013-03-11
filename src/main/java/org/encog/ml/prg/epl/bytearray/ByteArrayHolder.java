package org.encog.ml.prg.epl.bytearray;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import org.encog.Encog;
import org.encog.EncogError;
import org.encog.ml.prg.epl.EPLHolder;
import org.encog.ml.prg.epl.EPLUtil;
import org.encog.ml.prg.epl.OpCodeHeader;
import org.encog.util.EngineArray;
import org.encog.util.text.Base64;

public class ByteArrayHolder implements EPLHolder, Serializable {
	private final byte[] code;
	private final int maxIndividualFrames;
	private final int maxIndividualSize;
	private boolean invalid;

	public ByteArrayHolder(final int theMaxIndividualFrames) {
		this.maxIndividualFrames = theMaxIndividualFrames;
		this.maxIndividualSize = this.maxIndividualFrames
				* EPLHolder.FRAME_SIZE;
		this.code = new byte[this.maxIndividualSize];
	}

	@Override
	public void copy(final EPLHolder sourceHolder, final int sourceIndex,
			final int targetIndex, final int size) {
		if (!this.invalid) {
			final int absoluteSourceIndex = sourceIndex * EPLHolder.FRAME_SIZE;
			final int absoluteTargetIndex = targetIndex * EPLHolder.FRAME_SIZE;

			if (absoluteTargetIndex + size < this.maxIndividualSize) {
				EngineArray.arrayCopy(
						((ByteArrayHolder) sourceHolder).getCode(),
						absoluteSourceIndex, this.code, absoluteTargetIndex,
						size * EPLHolder.FRAME_SIZE);
			} else {
				this.invalid = true;
			}
		}
	}

	@Override
	public void deleteSubtree(final int index, final int size) {
		if (!this.invalid) {
			final int targetIndex = index * EPLHolder.FRAME_SIZE;
			final int sourceIndex = targetIndex + size * EPLHolder.FRAME_SIZE;
			final int remaining = this.maxIndividualSize - sourceIndex;
			EngineArray.arrayCopy(this.code, sourceIndex, this.code,
					targetIndex, remaining);
		}

	}

	@Override
	public int fromBase64(final String str) {
		try {
			final byte[] b = Base64.decode(str);
			if (b.length > this.maxIndividualSize) {
				throw new EncogError(
						"Can't decode program, it is too large.  Set the max individual size higher.");
			}
			EngineArray.arrayCopy(b, 0, this.code, 0, b.length);
			return b.length / EPLHolder.FRAME_SIZE;
		} catch (final IOException e) {
			throw new EncogError(e);
		}
	}

	/**
	 * @return the code
	 */
	public byte[] getCode() {
		return this.code;
	}

	@Override
	public int getMaxIndividualFrames() {
		return this.maxIndividualFrames;
	}

	@Override
	public int getMaxIndividualSize() {
		return this.maxIndividualSize;
	}

	@Override
	public int getPopulationSize() {
		return this.code.length / this.maxIndividualSize;
	}

	@Override
	public void insert(final int index, final int size) {
		if (!this.invalid) {
			final int sourceIndex = index * EPLHolder.FRAME_SIZE;
			final int targetIndex = sourceIndex + size * EPLHolder.FRAME_SIZE;
			final int remaining = this.maxIndividualSize - targetIndex;
			if (remaining > 0) {
				EngineArray.arrayCopy(this.code, sourceIndex, this.code,
						targetIndex, remaining);
			} else if (remaining < 0) {
				this.invalid = true;
			}
		}
	}

	@Override
	public boolean isInvalid() {
		return this.invalid;
	}

	@Override
	public double readDouble(final int index) {
		final int absoluteIndex = index * EPLHolder.FRAME_SIZE;
		return EPLUtil.bytesToDouble(this.code, absoluteIndex);
	}

	@Override
	public void readNodeHeader(final int index, final OpCodeHeader header) {
		final int absoluteIndex = index * EPLHolder.FRAME_SIZE;
		header.setOpcode(EPLUtil.bytesToShort(this.code, absoluteIndex));
		header.setParam1(EPLUtil.bytesToInt(this.code, absoluteIndex + 2));
		header.setParam2(EPLUtil.bytesToShort(this.code, absoluteIndex + 6));
	}

	@Override
	public String readString(final int index, final int encodedLength) {
		try {
			final int absoluteIndex = index * EPLHolder.FRAME_SIZE;
			return new String(this.code, absoluteIndex, encodedLength,
					Encog.DEFAULT_ENCODING);
		} catch (final UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}

	@Override
	public String toBase64(final int programLength) {
		return Base64.encodeBytes(this.code, 0, programLength
				* EPLHolder.FRAME_SIZE);
	}

	@Override
	public void writeByte(final int index, final byte[] b) {
		if (!this.invalid) {
			final int absoluteIndex = index * EPLHolder.FRAME_SIZE;
			if (absoluteIndex + b.length < this.maxIndividualSize) {
				EngineArray.arrayCopy(b, 0, this.code, absoluteIndex, b.length);
			} else {
				this.invalid = true;
			}
		}

	}

	@Override
	public void writeDouble(final int index, final double value) {
		if (!this.invalid) {
			final int absoluteIndex = index * EPLHolder.FRAME_SIZE;
			if (absoluteIndex + 8 < this.maxIndividualSize) {
				EPLUtil.doubleToBytes(value, this.code, absoluteIndex);
			} else {
				this.invalid = true;
			}
		}
	}

	@Override
	public void writeNode(final int index, final short opcode,
			final int param1, final short param2) {
		if (!this.invalid) {
			final int absoluteIndex = index * EPLHolder.FRAME_SIZE;
			if (absoluteIndex + 8 < this.maxIndividualSize) {
				EPLUtil.shortToBytes(opcode, this.code, absoluteIndex);
				EPLUtil.intToBytes(param1, this.code, absoluteIndex + 2);
				EPLUtil.shortToBytes(param2, this.code, absoluteIndex + 6);
			} else {
				this.invalid = true;
			}
		}
	}

	@Override
	public void setInvalid(boolean b) {
		this.invalid = b;
	}
}
