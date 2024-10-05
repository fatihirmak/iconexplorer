package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class ResourceDirectoryString extends Struct {
	short length;
	String string;
	
	public ResourceDirectoryString() {
	}

	public ResourceDirectoryString(ByteBuffer stream) {
		super(stream);
		length = readShort();
		byte[] bytes = readBytes(length * 2);
		char[] chars = new char[length];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) ((bytes[i * 2 + 1] & 0xFF << 8) | bytes[i * 2] & 0xFF);  
		}
		string = new String(chars).trim();
	}
	
	public String getString() {
		return string.trim();
	}
}
