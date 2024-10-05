package us.irmak.win32.iconexplorer;

public abstract class Icon {
	private short resourceName;
	
	Icon(short resourceName) {
		this.resourceName = resourceName;
	}

	public abstract short getWidth();

	public abstract short getHeight();

	public abstract short getColorCount();

	public abstract short getPlanes();

	public abstract short getBitCount();

	public abstract int getSize();

	public int getResourceId() {
		return resourceName & 0xFFFF;
	}
	
	@Override
	public String toString() {
		return String.format("Icon [id=%d(%d), dimension=%dx%d, bpp=%d, size=%d]", getResourceId(), resourceName, getWidth(), getHeight(), getBitCount(), getSize());
	}
}
