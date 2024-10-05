package us.irmak.win32.iconexplorer.pe;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class Bitmap {
	private byte[] data;
	private int width;
	private int height;
	private int bpp;
	
	private Bitmap mask;
	private RGBQuad[] colorTable;
	
	public Bitmap(byte[] data, int width, int height, int bpp) {
		super();
		this.data = data;
		this.width = width;
		this.height = height;
		this.bpp = bpp;
	}
	
	public Bitmap(ByteBuffer buffer, int width, int height, int bpp) {
		super();
		this.width = width;
		this.height = height;
		this.bpp = bpp;
		
		int bitstride = calculateStride(width, bpp);
		int datasize = bitstride * height;
		
		data = new byte[datasize];
		buffer.get(data, 0, datasize);
	}
	
	private int[] transform() {
		int[] target = new int[width * height * 4]; //ARGB -> 4 bytes per pixel
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int value = getValue(j, i);
				if (colorTable != null) {
					value = getColorFromTable(value); //value is index on the table
				}
				if (mask != null) {
					value |= mask.getValue(j, i) == 1 ? 0x0 : 0xFF000000;
				}
				target[(height - i - 1) * width + j] = value;
			}
		}
		return target;
	}
	
	public void setMask(Bitmap mask) {
		this.mask = mask;
	}
	
	public void setColorTable(RGBQuad[] colorTable) {
		this.colorTable = colorTable;
	}
	
	private int getColorFromTable(int index) {
		RGBQuad rgb = colorTable[index];
		return (rgb.getRed() & 0xFF) << 16 | (rgb.getGreen() & 0xFF) << 8 | (rgb.getBlue() & 0xFF);  
	}

	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	image.setRGB(0, 0, width, height, transform(), 0, width);
    	return image;
	}
	
	static int calculateStride(int width, int bpp) {
		return ((width * bpp + 31) & ~31) >> 3;
	}
	
	int getValue(int x, int y) {
		int value = 0;
		int stride = calculateStride(width, bpp);
		int size = Math.max(bpp / 8, 1);
		int index = y * stride + x * bpp / 8;
		for (int i = 0; i < size; i++) {
			value |= (data[index + i] & 0xFF) << i * 8;
		}
		if (bpp < 8) {
			int shiftcount = x * bpp % 8 / bpp; //yes x/bpp*bpp not same as x, as x/bpp rounded down to integer
			int subtractFrom = 8 / bpp - 1;
			int shift = (subtractFrom - shiftcount) * bpp;
			value= (value >> shift) & ((2 << bpp - 1) - 1);
		}
		return value;
	}
}
