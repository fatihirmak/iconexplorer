package us.irmak.win32.iconexplorer;

import static java.util.stream.Collectors.toList;

import java.util.List;

import us.irmak.win32.iconexplorer.pe.IconDirResource;
import us.irmak.win32.iconexplorer.pe.ResourceDirectoryEntry.ResourceId;

public class NativeIconGroup extends IconGroup {
	private List<Icon> icons;
	private ResourceId resourceName;
	
	public NativeIconGroup(ResourceId resourceName, IconDirResource resource) {
		super((short)0);
		this.resourceName = resourceName;
		icons = resource.getImageDirectory().stream().map(entry -> new NativeIcon(entry.getId(), entry)).collect(toList());
	}

	@Override
	public List<Icon> getIcons() {
		return icons;
	}

	@Override
	public short getType() {
		return 0;
	}
	
	@Override
	public String getResourceName() {
		return resourceName.toString();
	}
}
