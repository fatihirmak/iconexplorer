package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class SectionHeader extends Struct {
	byte[] name = new byte[8];
	int virtualSize;
	int virtualAddress;
	int sizeOfRawData;
	int rawDataLocation;
	int pointerToRelocations;
	int pointerToLinenumbers;
	short numberOfRelocations;
	short numberOfLinenumbers;
	int characteristicsFlag;
	
	Object sectionData;
	ByteBuffer sectionDataBuffer;
	
	public SectionHeader() {
		super();
	}
	public SectionHeader(ByteBuffer stream) {
		super(stream);
		name = readBytes(8);
		virtualSize = readInt();
		virtualAddress = readInt();
		sizeOfRawData = readInt();
		rawDataLocation = readInt();
		pointerToRelocations = readInt();
		pointerToLinenumbers = readInt();
		numberOfRelocations = readShort();
		numberOfLinenumbers = readShort();
		characteristicsFlag = readInt();
		
		sectionDataBuffer = subBuffer(rawDataLocation, sizeOfRawData);
	}
	
	public String getName() {
		return new String(name).trim();
	}
	
	public Object getSection() {
		if (".rsrc".equals(getName())) {
			return new ResourceDirectory(sectionDataBuffer);
		}
		return null;
	}
	
}
