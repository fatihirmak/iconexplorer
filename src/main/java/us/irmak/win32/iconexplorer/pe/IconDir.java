package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class IconDir extends Struct {
	short reserved;
	short imageType;
	short imageCount;
	IconDirEntry[] imageDirectory;
	
	public IconDir() {
	}
	
	public IconDir(ByteBuffer stream) {
		super(stream);
		reserved = readShort();
		imageType = readShort();
		imageCount = readShort();
		imageDirectory = new IconDirEntry[imageCount];
		for (int i = 0; i < imageCount; i++) {
			imageDirectory[i] = new IconDirEntry(stream);
		}
	}
}
