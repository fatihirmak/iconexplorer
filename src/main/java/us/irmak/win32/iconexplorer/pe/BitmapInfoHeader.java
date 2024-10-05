package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

@FieldOrder({"size", "width", "height", "planes", "bitCount", "compression", "imageSize", "xPelsPerMeter", "yPelsPerMeter", "colorsUsed", "colorsImportant"})
public class BitmapInfoHeader extends Struct {
	int size;
	int width;
	int height;
	short planes;
	short bitCount;
	int compression;
	int imageSize;
	int xPelsPerMeter;
	int yPelsPerMeter;
	int colorsUsed;
	int colorsImportant;
	
	public BitmapInfoHeader() {
		super();
	}
	
	public BitmapInfoHeader(ByteBuffer stream) {
		super(stream);
		readValues();
		if (size != 0x28) {
			throw new RuntimeException("Wrong header is used.");
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public short getPlanes() {
		return planes;
	}

	public short getBitCount() {
		return bitCount;
	}

	public int getCompression() {
		return compression;
	}

	public int getImageSize() {
		return imageSize;
	}

	public int getxPelsPerMeter() {
		return xPelsPerMeter;
	}

	public int getyPelsPerMeter() {
		return yPelsPerMeter;
	}

	public int getColorsUsed() {
		return colorsUsed;
	}

	public int getColorsImportant() {
		return colorsImportant;
	}
	
	@Override
	public String toString() {
		return String.format("%dx%d [bpp=%d] [size=%d] [comp=%d] [colors=%d]", getWidth(), getHeight(), getBitCount(), getImageSize(), getCompression(), getColorsUsed());
	}
}
