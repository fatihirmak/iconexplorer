package us.irmak.win32.iconexplorer.pe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;

import us.irmak.win32.iconexplorer.NativeIconResource;

public class MainTest {
	@Test
	@Ignore
	public void test() throws IOException {
		File file = new File("c:\\Users\\irmfatih\\vscode\\winapitest\\OneDrive.ico");
		
		IconDir iconDir = Struct.load(IconDir.class, new FileInputStream(file));
		
		for (int k = 0; k < iconDir.imageCount; k++) {
			IconDirEntry id = iconDir.imageDirectory[k];
			
			
		}
	}
	
	private void writeImage(byte[] data, int width, int height) throws IOException {
		data = Arrays.copyOfRange(data, 0, width*height*4);
		
		int[] rgb = new int[data.length/4];
		data[0] = 0x24;
		data[1] = 0x1C;
		data[2] = (byte) 0xED;
		for (int i = 0; i < rgb.length; i++) {
			int index = i*4;
			int dest = ((height - i / width) - 1) * width + i % width;
			rgb[dest] |= (data[index+0]) & 0xFF; 
			rgb[dest] |= (data[index+1] << 8) & 0xFF00;
			rgb[dest] |= (data[index+2] << 16) & 0xFF0000;
			rgb[dest] |= (data[index+3] << 24) & 0xFF000000;
		}
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    	image.setRGB(0, 0, width, height, rgb, 0, width);
		ImageIO.write(image, "png", new File(String.format("C:\\Temp\\file-%dx%d.png", width, height)));
	}
	
	@Test
	public void testDll() throws IOException {
		File file = new File("c:\\Windows\\SystemResources\\imageres.dll.mun");
		if (file.exists()) {
			NativeIconResource resource = new NativeIconResource(file);
			resource.getIconGroups().stream().forEach(group -> {
				group.getIcons().stream().forEach(icon -> {
					if (icon.getResourceId() == 761) {
						System.out.println(group.getResourceName());
					}
					BufferedImage image = resource.getImage(icon);
					try {
						if (image != null) {
							//ImageIO.write(image, "png", new File(String.format("C:\\Temp\\file-%s-%dx%d-%dbpp.png", group.getResourceName(), icon.getWidth(), icon.getHeight(), icon.getBitCount())));
						}
					} catch (Exception e) {
						System.out.println(group.getResourceName());
						e.printStackTrace();
					}
				});
			});
		}
		
	}
}
