package us.irmak.win32.iconexplorer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import us.irmak.win32.iconexplorer.jna.User32Extension.GroupIconDirectory;

public class WinIconGroup extends IconGroup {
	private List<Icon> icons;
	private GroupIconDirectory directory;
	
	public WinIconGroup(short resourceName, GroupIconDirectory directory) {
		super(resourceName);
		this.directory = directory;
		icons = Arrays.asList(directory.idEntries).stream().map(e -> new WinIcon(e, resourceName)).collect(Collectors.toList());
	}
	
	public List<Icon> getIcons() {
		return icons;
	}
	
	public short getType() {
		return directory.idType.shortValue();
	}

}
