package us.irmak.win32.iconexplorer;

import java.util.List;

public abstract class IconGroup {
	private short resourceName;
	
	IconGroup(short resourceName) {
		this.resourceName = resourceName;
	}
	
	public abstract List<Icon> getIcons();
	
	public abstract  short getType();
	
	public String getResourceName() {
		return String.valueOf(resourceName & 0xFFFF);
	}
}
