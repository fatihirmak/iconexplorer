package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

public class RGBQuad extends Struct {
	byte blue;
	byte green;
	byte red;
	byte reserved;
	
	public RGBQuad() {
	}

	public RGBQuad(ByteBuffer stream) {
		super(stream);
		blue = readByte();
		green = readByte();
		red = readByte();
		reserved = readByte();
	}

	public byte getBlue() {
		return blue;
	}

	public byte getGreen() {
		return green;
	}

	public byte getRed() {
		return red;
	}

	public byte getReserved() {
		return reserved;
	}
	
	public static RGBQuad rgb(int value) {
		RGBQuad rgbquad = new RGBQuad();
		rgbquad.red = (byte) ((value >> 16) & 0xFF);
		rgbquad.green = (byte) ((value >> 8) & 0xFF);
		rgbquad.blue = (byte) ((value >> 0) & 0xFF);
		return rgbquad;
	}
	
	@Override
	public String toString() {
		return Integer.toHexString((reserved & 0xFF) << 24 | (red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF)).toUpperCase();
	}
} 
