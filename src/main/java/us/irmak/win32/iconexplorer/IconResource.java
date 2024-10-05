package us.irmak.win32.iconexplorer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public abstract class IconResource {
	protected File resourceFile;
	
	public IconResource(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(file.getAbsolutePath() + " doesn't exist.");
		}
		resourceFile = file;
	}
	
	public abstract int size();
	
	public abstract List<IconGroup> getIconGroups();
	
	public abstract BufferedImage getImage(Icon icon);
}
