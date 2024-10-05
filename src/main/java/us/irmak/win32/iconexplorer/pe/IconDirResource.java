package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class IconDirResource extends Struct {
	short reserved;
	short imageType;
	short imageCount;
	IconDirResourceEntry[] imageDirectory;
	
	public IconDirResource() {
	}
	
	public IconDirResource(ByteBuffer stream) {
		super(stream);
		reserved = readShort();
		imageType = readShort();
		imageCount = readShort();
		imageDirectory = new IconDirResourceEntry[imageCount];
		for (int i = 0; i < imageCount; i++) {
			imageDirectory[i] = new IconDirResourceEntry(stream);
		}
	}

	public List<IconDirResourceEntry> getImageDirectory() {
		return Arrays.asList(imageDirectory);
	}
}
