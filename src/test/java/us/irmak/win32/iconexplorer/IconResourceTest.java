package us.irmak.win32.iconexplorer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Ignore;
import org.junit.Test;

import us.irmak.win32.iconexplorer.IconGroup;
import us.irmak.win32.iconexplorer.IconResource;

public class IconResourceTest {
	File root = new File("C:\\Windows\\system32");
	File imageres = new File(root, "imageres.dll");
	File output = new File("C:\\Temp\\icons");
	@Test
	@Ignore
	public void test() throws FileNotFoundException {
		try (IconResource ir = new IconResource(imageres)) {
			IconGroup group = ir.getIconGroups().stream().filter(gr -> gr.getResourceName() == 5320).findFirst().orElseThrow();
			group.getIcons().stream().forEach(icon -> write(ir.getImage(icon), new File(String.format("C:\\temp\\%s (%d)-%dbit-%dx%d.png", 
				imageres.getName(), group.getResourceName(), icon.getBitCount(), icon.getWidth(), icon.getHeight()))));
		}
	}
	
	private void write(BufferedImage image, File location) {
		try {
			ImageIO.write(image, "png", location);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void writeIcon(BufferedImage image, Map<String, String> attributes) {
		try {
			/*String filename = "{s}\\{g}\\{i}\\{w}x{h}-{bpp}bpp.png";
			Pattern p = Pattern.compile("\\{([a-z]+\\}");
			Matcher m = p.matcher(filename);
			while (m.matches()) {
				m.group(1);
			}*/
			File file = new File(output, String.format("%s\\%s\\%sx%s-%sbpp.png", attributes.get("s"),
					attributes.get("g"), attributes.get("w"), attributes.get("h"), attributes.get("bpp")));
			file.getParentFile().mkdirs();
			System.out.println(file);
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	@Ignore
	public void testAll() throws FileNotFoundException {
		try (IconResource ir = new IconResource(imageres)) {
			Map<String, String> attributes = new HashMap<>();
			attributes.put("s", imageres.getName());
			ir.getIconGroups().forEach(ig -> {
				attributes.put("g", String.valueOf(ig.getResourceName()));
				ig.getIcons().stream().forEach(icon -> {
					attributes.put("i", String.valueOf(icon.getResourceId()));
					attributes.put("bpp", String.valueOf(icon.getBitCount()));
					attributes.put("w", String.valueOf(icon.getWidth()));
					attributes.put("h", String.valueOf(icon.getHeight()));
					writeIcon(ir.getImage(icon), attributes);
				});
			});
			
			
			/*
			.flatMap(l -> l.stream())
				.forEach(icon -> writeIcon(ir.getImage(icon), icon));
			
			IconGroup group = ir.getIconGroups().stream().filter(gr -> gr.getResourceName() == 101).findFirst().orElseThrow();
			group.getIcons().stream().forEach(icon -> write(ir.getImage(icon), new File(String.format("C:\\temp\\%s (%d)-%dbit-%dx%d.png", 
				imageres.getName(), group.getResourceName(), icon.getBitCount(), icon.getWidth(), icon.getHeight()))));
				*/
		}
	}
	
	@Test
	@Ignore
	public void testJava() {
		int[] widths = new int[] {16, 32, 48, 64, 96};
		for (int i = 0; i < widths.length; i++) {
			int n = widths[i];
			System.out.println((int) Math.ceil(n / 32f) * 4);
		}
	}
	
	@Test
	@Ignore
	public void testShellIcon() {
		write(Util.getShellIcon("dll"), new File("C:\\Temp\\dllicon.png"));
		write(Util.getShellIcon("exe"), new File("C:\\Temp\\exeicon.png"));
		write(Util.getFolderShellIcon(), new File("C:\\Temp\\foldericon.png"));
	}
}
