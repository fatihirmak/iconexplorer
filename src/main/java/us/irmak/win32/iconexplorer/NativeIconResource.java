package us.irmak.win32.iconexplorer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import us.irmak.win32.iconexplorer.pe.Bitmap;
import us.irmak.win32.iconexplorer.pe.BitmapInfoHeader;
import us.irmak.win32.iconexplorer.pe.IconDirResource;
import us.irmak.win32.iconexplorer.pe.PEFile;
import us.irmak.win32.iconexplorer.pe.RGBQuad;
import us.irmak.win32.iconexplorer.pe.ResourceDirectoryEntry.ResourceId;
import us.irmak.win32.iconexplorer.pe.ResourceManager;
import us.irmak.win32.iconexplorer.pe.ResourceType;
import us.irmak.win32.iconexplorer.pe.Struct;

public class NativeIconResource extends IconResource {
	private ResourceManager resourceManager;
	private List<IconGroup> iconGroups;
	public NativeIconResource(File file) throws FileNotFoundException {
		super(file);
		try {
			resourceManager = new ResourceManager(Struct.load(PEFile.class, new FileInputStream(file)));
			List<ResourceId> groups = resourceManager.getResourceList(ResourceType.GROUP_ICON);
			iconGroups = groups.stream().map(this::newGroup).collect(Collectors.toList());
		} catch(Exception e) {
			throw e;
		}
	}
	
	private NativeIconGroup newGroup(ResourceId id) {
		ByteBuffer buffer = resourceManager.getResourceData(ResourceType.GROUP_ICON, id);
		return new NativeIconGroup(id, new IconDirResource(buffer));
	}

	@Override
	public int size() {
		return iconGroups.size();
	}

	@Override
	public List<IconGroup> getIconGroups() {
		return iconGroups;
	}

	@Override
	public BufferedImage getImage(Icon icon) {
		ByteBuffer buffer = resourceManager.getResourceData(ResourceType.ICON, icon.getResourceId());
		buffer.mark();
		int firstInt = buffer.getInt();
		buffer.reset();
		if (firstInt == 0x474E5089) {//PNG signature
			try {
				byte[] bytes = new byte[buffer.capacity()];
				buffer.get(bytes);
				return ImageIO.read(new ByteArrayInputStream(bytes));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			BitmapInfoHeader header = new BitmapInfoHeader(buffer);

			int width = icon.getWidth();
			int height = icon.getHeight();
			int bpp = header.getBitCount();
			
			RGBQuad[] colorTable = null;
			if (bpp <= 8) {
				int colors = header.getColorsUsed() == 0 ? 2 << bpp - 1 : header.getColorsUsed();
				colorTable = new RGBQuad[colors];
				for (int i = 0; i < colors; i++) {
					colorTable[i] = new RGBQuad(buffer);
				}
			}
			
			Bitmap bitmap = new Bitmap(buffer, width, height, bpp);
			if (colorTable != null) {
				bitmap.setColorTable(colorTable);
			}
			if (bpp < 32) {
				Bitmap mask = new Bitmap(buffer, width, height, 1);
				bitmap.setMask(mask);
			}
			
			return bitmap.getImage();
		} 
	}
	
	public static boolean isPEFormat(File file) {
		try (FileInputStream fis = new FileInputStream(file)){
			byte[] bytes = new byte[2];
			fis.read(bytes);
			return bytes[0] == 'M' && bytes[1] == 'Z';
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
