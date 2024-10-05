package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class IconDirEntry extends Struct {
	byte width;
	byte height;
	byte colors;
	byte reserved;
	short colorPlanes;
	short bitsPerPixel;
	int imageSize;
	int offset;
	BitmapInfoHeader header;
	
	public IconDirEntry() {
	}
	
	public IconDirEntry(ByteBuffer stream) {
		super(stream);
		width = readByte();
		height = readByte();
		colors = readByte();
		reserved = readByte();
		colorPlanes = readShort();
		bitsPerPixel = readShort();
		imageSize = readInt();
		offset = readInt();
		
		header = new BitmapInfoHeader(subBuffer(offset, imageSize));
	}
}
