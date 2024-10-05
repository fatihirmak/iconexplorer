package us.irmak.win32.iconexplorer;

import us.irmak.win32.iconexplorer.pe.IconDirResourceEntry;

public class NativeIcon extends Icon {
	private IconDirResourceEntry entry;
	public NativeIcon(short resourceName, IconDirResourceEntry entry) {
		super(resourceName);
		this.entry = entry;
	}

	@Override
	public short getWidth() {
		return entry.getWidth() == 0 ? 256 : (short) (entry.getWidth() & 0xFF);
	}

	@Override
	public short getHeight() {
		return entry.getHeight() == 0 ? 256 : (short) (entry.getHeight() & 0xFF);
	}

	@Override
	public short getColorCount() {
		return (short) entry.getColors();
	}

	@Override
	public short getPlanes() {
		return entry.getColorPlanes();
	}

	@Override
	public short getBitCount() {
		return entry.getBitsPerPixel();
	}

	@Override
	public int getSize() {
		return entry.getImageSize();
	}

}
