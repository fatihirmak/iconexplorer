package us.irmak.win32.iconexplorer;

import us.irmak.win32.iconexplorer.jna.User32Extension.GroupIconDirectoryEntry;

public class WinIcon extends Icon {
	private GroupIconDirectoryEntry entry;
	
	public WinIcon(GroupIconDirectoryEntry entry, short resourceName) {
		super(resourceName);
		this.entry = entry; 
	}
	
	public short getWidth() {
		short width = entry.bWidth.shortValue();
		return width == 0 ? 256 : width;
	}

	public short getHeight() {
		short height = entry.bHeight.shortValue();
		return height == 0 ? 256 : height;
	}

	public short getColorCount() {
		return entry.bColorCount.shortValue();
	}

	public short getPlanes() {
		return entry.wPlanes.shortValue();
	}

	public short getBitCount() {
		return entry.wBitCount.shortValue();
	}

	public int getSize() {
		return entry.dwBytesInRes.intValue();
	}

	public int getResourceId() {
		return entry.nId.intValue();
	}
}
