package us.irmak.win32.iconexplorer;

import com.sun.jna.Pointer;

import us.irmak.win32.iconexplorer.jna.User32Extension;

public enum ResourceType {
	ICON_GROUP(User32Extension.RT_GROUP_ICON), ICON(User32Extension.RT_ICON);
	private int value;
	private ResourceType(int value) {
		this.value = value;
	}
	public int value() {
		return value;
	}
	
	public Pointer getPointer() {
		return new Pointer(value);
	}
}
