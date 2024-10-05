package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class IconDirResourceEntry extends Struct {
	byte width;
	byte height;
	byte colors;
	byte reserved;
	short colorPlanes;
	short bitsPerPixel;
	int imageSize;
	short id;
	BitmapInfoHeader header;
	
	public IconDirResourceEntry() {
	}
	
	public IconDirResourceEntry(ByteBuffer stream) {
		super(stream);
		width = readByte();
		height = readByte();
		colors = readByte();
		reserved = readByte();
		colorPlanes = readShort();
		bitsPerPixel = readShort();
		imageSize = readInt();
		id = readShort();
	}

	public byte getWidth() {
		return width;
	}

	public byte getHeight() {
		return height;
	}

	public byte getColors() {
		return colors;
	}

	public short getColorPlanes() {
		return colorPlanes;
	}

	public short getBitsPerPixel() {
		return bitsPerPixel;
	}

	public int getImageSize() {
		return imageSize;
	}

	public short getId() {
		return id;
	}

	public BitmapInfoHeader getHeader() {
		return header;
	}
}
