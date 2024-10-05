package us.irmak.win32.iconexplorer.pe;

import static org.junit.Assert.assertEquals;
import static us.irmak.win32.iconexplorer.pe.RGBQuad.rgb;

import java.awt.image.BufferedImage;

import org.junit.Test;

public class BitmapTest {

	@Test
	public void testGetValue8Bit() {
		byte[] data = new byte[] {0, 1, 0, 0, 2, 3, 0, 0};
		Bitmap bitmap = new Bitmap(data, 2, 2, 8);
		assertEquals(bitmap.getValue(0, 0), 0);
		assertEquals(bitmap.getValue(1, 0), 1);
		assertEquals(bitmap.getValue(0, 1), 2);
		assertEquals(bitmap.getValue(1, 1), 3);
	}

	@Test
	public void testGetValue4Bit() {
		byte[] data = new byte[] {0x1, 0, 0, 0, 0x23, 0, 0, 0};
		Bitmap bitmap = new Bitmap(data, 2, 2, 4);
		assertEquals(0, bitmap.getValue(0, 0));
		assertEquals(1, bitmap.getValue(1, 0));
		assertEquals(2, bitmap.getValue(0, 1));
		assertEquals(3, bitmap.getValue(1, 1));
	}

	@Test
	public void testGetValue2Bit() {
		byte[] data = new byte[] {0x10, 0, 0, 0, (byte)0xB0, 0, 0, 0};
		Bitmap bitmap = new Bitmap(data, 2, 2, 2);
		assertEquals(0, bitmap.getValue(0, 0));
		assertEquals(1, bitmap.getValue(1, 0));
		assertEquals(2, bitmap.getValue(0, 1));
		assertEquals(3, bitmap.getValue(1, 1));
	}

	@Test
	public void test8BitBitmap() {
		byte[] data = new byte[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF};
		Bitmap bitmap = new Bitmap(data, 4, 4, 8);
		
		byte[] maskdata = new byte[] {(byte)0xF0, 0, 0, 0, 0x70, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0};
		Bitmap mask = new Bitmap(maskdata, 4, 4, 1);
		bitmap.setMask(mask);
		
		BufferedImage image = bitmap.getImage();
		assertEquals(0xFF000000, image.getRGB(0, 0));
		assertEquals(0xFF000001, image.getRGB(1, 0));
		assertEquals(0x00000004, image.getRGB(0, 1));
	}

	@Test
	public void testColorTable() {
		byte[] data = new byte[] {0x10, 0, 0, 0, (byte) 0xB0, 0, 0, 0};
		Bitmap bitmap = new Bitmap(data, 2, 2, 2);
		
		RGBQuad[] table = new RGBQuad[] {rgb(0xFF1122), rgb(0xBBCCDD), rgb(0xAA00EE), rgb(0x0)};
		bitmap.setColorTable(table);
		
		BufferedImage image = bitmap.getImage();
		assertEquals(0xFF1122, image.getRGB(0, 0));
		assertEquals(0xBBCCDD, image.getRGB(1, 0));
		assertEquals(0xAA00EE, image.getRGB(0, 1));
		assertEquals(0x0, image.getRGB(1, 1));
	}
}
