package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class ResourceDataEntry extends Struct {
	int dataRva;
	int size;
	int codePage;
	int reserved;
	
	public ResourceDataEntry() {
	}

	public ResourceDataEntry(ByteBuffer stream) {
		super(stream);
		dataRva = readInt();
		size = readInt();
		codePage = readInt();
		reserved = readInt();
	}
	
	public ByteBuffer getData(int base) {
		return subBuffer(dataRva - base, size);
		//return markAndSet(dataRva - base).apply(() -> readBytes(size)).reset();
	}
}
