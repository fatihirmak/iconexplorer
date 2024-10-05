package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

@FieldOrder({"signature", "machine", "numberOfSections", "timestamp", "symbolTablePointer", "numberOfSymbols", 
				"optionalHeaderSize", "characteristicsFlag"})
public class PEHeader extends Struct {
	int signature;
	short machine;
	short numberOfSections;
	int timestamp;
	int symbolTablePointer;
	int numberOfSymbols;
	short optionalHeaderSize;
	short characteristicsFlag;
	
	SectionHeader[] sectionHeaders; 
	public PEHeader() {
		super();
	}
	public PEHeader(ByteBuffer stream) {
		super(stream);
		readValues();
	}
	
	
}
