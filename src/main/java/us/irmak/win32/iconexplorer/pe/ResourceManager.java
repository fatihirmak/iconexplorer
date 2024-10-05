package us.irmak.win32.iconexplorer.pe;

import static java.util.stream.Collectors.toList;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import us.irmak.win32.iconexplorer.pe.ResourceDirectoryEntry.ResourceId;

public class ResourceManager {
	
	private PEFile pe;
	
	public ResourceManager(PEFile pe) {
		this.pe = pe;
	}
	
	public List<ResourceId> getResourceList(ResourceType type) {
		if (pe.resourceDirectory == null) {
			return Collections.emptyList();
		}
		return pe.resourceDirectory.getEntries().stream().filter(e -> e.getResourceId().equals(type.getId()))
					.map(e -> e.getChildDirectory().getEntries()).flatMap(Collection::stream)
					.map(ResourceDirectoryEntry::getResourceId)
					.flatMap(Stream::of)
					.collect(toList());
	}
	
	private int getBase() {
		return pe.getSectionHeader(".rsrc").orElseThrow().virtualAddress;
	}
	
	public ByteBuffer getResourceData(ResourceType type, Object id) {
		ResourceDataEntry data = findResourceDataEntry(type, id);
		return data.getData(getBase());
	}
	
	public ResourceDataEntry findResourceDataEntry(ResourceType type, Object id) {
		ResourceDirectoryEntry resources = findDirectoryEntry(pe.resourceDirectory, e -> e.getResourceId().equals(type.getId()));
		ResourceDirectoryEntry group = findDirectoryEntry(resources.getChildDirectory(), e -> e.getResourceId().equals(id));
		ResourceDirectoryEntry lang = findDirectoryEntry(group.getChildDirectory(), e -> true);
		return lang.getDataEntry();
	}
	/*
	public ResourceDirectoryEntry findDirectoryEntry(ResourceDirectory directory, ResourceId id) {
		return directory.getEntries().stream().filter(entry -> entry.getResourceId().equals(id)).findFirst().orElseThrow();
	}
	*/
	public ResourceDirectoryEntry findDirectoryEntry(ResourceDirectory directory, Predicate<ResourceDirectoryEntry> predicate) {
		return directory.getEntries().stream().filter(predicate).findFirst().orElseThrow();
	}
}
