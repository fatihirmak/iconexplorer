package us.irmak.win32.iconexplorer;

import java.awt.image.BaseMultiResolutionImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.WeakHashMap;

public class IconManager {
	public static final File WINDIR = new File(System.getenv("windir"));
	private static WeakHashMap<File, BaseMultiResolutionImage> ICON_CACHE = new WeakHashMap<>();
	
	private IconResource createIconResource(File resourceFile) {
		try {
			return new NativeIconResource(resourceFile);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public BaseMultiResolutionImage getResourceIcon(File resourceFile, Object resourceId) {
		IconResource resource = createIconResource(resourceFile);
		MultiResolutionImageToolkit toolkit = new MultiResolutionImageToolkit(resource);
		return resource.getIconGroups().stream()
			.filter(gr -> gr.getResourceName().equals(resourceId))
			.map(gr -> new BaseMultiResolutionImage(toolkit.getImages(gr)))
			.findAny()
			.or(() -> Optional.of(getResourceIcon(getAlternateResource(resourceFile).orElseThrow(), resourceId))).get();
	}
	
	public BaseMultiResolutionImage getShellIcon(File file) {
		return ICON_CACHE.computeIfAbsent(file, f -> new BaseMultiResolutionImage(Util.getShellIcon(file)));
	}
	
	private Optional<File> getAlternateResource(File resourceFile) {
		File system32 = new File(WINDIR, "system32");
		if (system32.equals(resourceFile.getParentFile()) && resourceFile.getAbsolutePath().endsWith(".dll")) {
			return Optional.of(new File(new File(WINDIR, "SystemResources"), resourceFile.getName() + ".mun"));
		}
		return Optional.empty();
	}
}
