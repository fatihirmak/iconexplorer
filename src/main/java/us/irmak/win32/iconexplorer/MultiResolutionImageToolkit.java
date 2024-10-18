package us.irmak.win32.iconexplorer;

import static java.util.stream.Collectors.toList;

import java.awt.Image;
import java.util.List;

public class MultiResolutionImageToolkit {
	private static final int[] BPP = new int[] {32, 24, 16, 8, 4, 2, 1};
	private IconResource resource;
	
	public MultiResolutionImageToolkit(IconResource resource) {
		this.resource = resource;
	}

	public Image[] getImages(IconGroup group) {
		Image[] images = null;
		int index = 0;
		while ((images == null || images.length == 0) && index < BPP.length) {
			images = getImages(group, BPP[index++]);
		}
		if (images == null || images.length == 0) {
			throw new RuntimeException("No applicable image was found");
		}
		return images;
	}
	
	public Image[] getImages(IconGroup group, int bpp) {
		List<Image> images = group.getIcons().stream()
								.filter(icon -> icon.getBitCount() == bpp)
								.sorted((l, r) -> Integer.compare(l.getWidth(), r.getWidth()))
								.map(resource::getImage)
								.collect(toList());
		return images.toArray(new Image[images.size()]);
	}
}
