package us.irmak.win32.iconexplorer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import us.irmak.win32.iconexplorer.jna.GroupIconDirectory;

public class IconGroup {
	private List<Icon> icons;
	private GroupIconDirectory directory;
	private short resourceName;
	
	IconGroup(short resourceName, GroupIconDirectory directory) {
		this.directory = directory;
		this.resourceName = resourceName;
		icons = Arrays.asList(directory.idEntries).stream().map(e -> new Icon(e, resourceName)).collect(Collectors.toList());
	}
	
	public List<Icon> getIcons() {
		return icons;
	}
	
	public short getType() {
		return directory.idType.shortValue();
	}
	
	public short getResourceName() {
		return resourceName;
	}
}
