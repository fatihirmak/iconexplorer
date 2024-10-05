package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;
import java.util.Optional;

public class ResourceDirectoryEntry extends Struct {
	int firstField; //either name offset or integer id
	int secondField; // either data entry offset or subdirectory offset
	
	int integerId = -1;
	ResourceDirectoryString stringId;
	
	ResourceDirectory childDirectory;

	ResourceDataEntry dataEntry;
	
	public ResourceDirectoryEntry() {
	}

	public ResourceDirectoryEntry(ByteBuffer stream) {
		super(stream);
		firstField = readInt();
		secondField = readInt();
		if ((0x80000000 & firstField) == 0x80000000) {
			int address = 0x80000000 ^ firstField;
			stringId = markAndSet(address).apply(() -> new ResourceDirectoryString(stream)).reset();
		} else {
			integerId = firstField;
		}
		
		if ((0x80000000 & secondField) == 0x80000000) {
			int address = 0x80000000 ^ secondField;
			childDirectory = markAndSet(address).apply(() -> new ResourceDirectory(stream)).reset();
		} else {
			dataEntry = markAndSet(secondField).apply(() -> new ResourceDataEntry(stream)).reset();
		}
	}
	
	public boolean isLeaf() {
		return childDirectory == null;
	}
	
	public boolean isNamedEntry() {
		return stringId != null;
	}
	
	public Optional<String> getName() {
		if (stringId == null) return Optional.empty();
		return Optional.of(stringId.getString());
	}
	
	public ResourceId getResourceId() {
		return new ResourceId();
	}
	
	public ResourceDirectory getChildDirectory() {
		return childDirectory;
	}

	public ResourceDataEntry getDataEntry() {
		return dataEntry;
	}
	
	public class ResourceId {
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof Integer) {
				return ((Integer) obj).equals(integerId);
			} else if (obj instanceof String) {
				if (stringId == null) {
					return false;
				}
				return ((String) obj).equals(stringId.getString());
			} else if (obj instanceof ResourceId) {
				ResourceId target = (ResourceId) obj;
				if (integerId != -1) {
					return target.getIntegerId() == integerId;
				} else if (stringId != null) {
					return stringId.getString().equals(target.getStringId());
				} else {
					return false;
				}
			}
			return false;
		}
		
		private int getIntegerId() {
			return integerId;
		}
		
		private String getStringId() {
			return stringId != null ? stringId.getString() : null;
		}
		
		@Override
		public String toString() {
			return integerId != -1 ? Integer.toString(integerId) : stringId.getString();
		}
	}
}
