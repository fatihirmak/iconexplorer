package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class OptionalHeader extends Struct {
	byte[] data;

	public OptionalHeader() {
		super();
	}

	public OptionalHeader(ByteBuffer stream) {
		super(stream);
	}
	
}
