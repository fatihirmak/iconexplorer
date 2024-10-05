package us.irmak.win32.iconexplorer.pe;

import static java.util.Collections.unmodifiableList;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@FieldOrder({"characteristics"})
public class ResourceDirectory extends Struct {
	int characteristics;
	int timestamp;
	short majorVersion;
	short minorVersion;
	short namedEntriesCount;
	short idEntriesCount;
	
	List<ResourceDirectoryEntry> entries = new ArrayList<>();
	
	public ResourceDirectory() {
	}

	public ResourceDirectory(ByteBuffer stream) {
		super(stream);
		characteristics = readInt();
		timestamp = readInt();
		majorVersion = readShort();
		minorVersion = readShort();
		namedEntriesCount = readShort();
		idEntriesCount = readShort();
		
		for (int i = 0; i < namedEntriesCount; i++) {
			entries.add(new ResourceDirectoryEntry(stream));
		}
		for (int i = 0; i < idEntriesCount; i++) {
			entries.add(new ResourceDirectoryEntry(stream));
		}
	}
	
	public Optional<ResourceDirectoryEntry> getEntry(int id) {
		return entries.stream().filter(e -> e.integerId == id).findFirst();
	}

	public int getCharacteristics() {
		return characteristics;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public short getMajorVersion() {
		return majorVersion;
	}

	public short getMinorVersion() {
		return minorVersion;
	}

	public short getNamedEntriesCount() {
		return namedEntriesCount;
	}

	public short getIdEntriesCount() {
		return idEntriesCount;
	}

	public List<ResourceDirectoryEntry> getEntries() {
		return unmodifiableList(entries);
	}

}
