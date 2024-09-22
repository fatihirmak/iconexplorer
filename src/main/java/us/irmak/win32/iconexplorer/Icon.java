package us.irmak.win32.iconexplorer;

import us.irmak.win32.iconexplorer.jna.GroupIconDirectoryEntry;

public class Icon {
	private GroupIconDirectoryEntry entry;
	private short resourceName;
	
	Icon(GroupIconDirectoryEntry entry, short resourceName) {
		this.entry = entry;
		this.resourceName = resourceName;
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
	
	@Override
	public String toString() {
		return String.format("Icon [id=%d(%d), dimension=%dx%d, bpp=%d, size=%d]", getResourceId(), resourceName, getWidth(), getHeight(), getBitCount(), getSize());
	}
}
