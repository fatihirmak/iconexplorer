package us.irmak.win32.iconexplorer;

import static java.util.stream.Collectors.toSet;

import java.awt.Dimension;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.swing.ImageIcon;

public class ImageGroup {
	private IconGroup group;
	private IconResource resource;
	private ImageIcon imageIcon;
	
	public ImageGroup(IconResource resource, IconGroup group) {
		this.group = group;
		this.resource = resource;
		
		int bestQualityBpp = getBPPs().stream().sorted((x, y) -> Integer.compare(y, x)).findFirst().get();
		List<BufferedImage> imageList = getImages(bestQualityBpp);
		if (imageList.size() > 1) {
			List<BufferedImage> tmpList = imageList.stream().filter(i -> i.getWidth() <= 64).toList();
			if (!tmpList.isEmpty()) {
				imageList = tmpList;
			}
		}
		BufferedImage[] array = imageList.toArray(new BufferedImage[0]);
		imageIcon = new VariantImageIcon(new BaseMultiResolutionImage(array), 32, 32);
	}
	
	public ImageIcon getImageIcon() {
		return imageIcon;
	}

	public String getGroupName() {
		return group.getResourceName();
	}

	public Set<Dimension> getSizes() {
		return group.getIcons().stream().map(icon -> new Dimension(icon.getWidth(), icon.getHeight())).collect(toSet());
	}
	
	public Set<Short> getBPPs() {
		return group.getIcons().stream().map(Icon::getBitCount).collect(toSet());
	}
	
	public List<BufferedImage> getImages(Dimension size) {
		return group.getIcons().stream()
				.filter(icon -> icon.getWidth() == size.width && icon.getHeight() == size.height)
				.map(resource::getImage)
				.toList();
	}
	
	public List<BufferedImage> getImages(Predicate<Icon> predicate) {
		return group.getIcons().stream()
				.filter(predicate)
				.map(resource::getImage)
				.toList();
	}
	
	public List<BufferedImage> getImages(Integer bpp) {
		return group.getIcons().stream()
				.filter(icon -> icon.getBitCount() == bpp)
				.map(resource::getImage)
				.toList();
	}
	
	public Optional<BufferedImage> getImage(Dimension size, Integer bpp) {
		return group.getIcons().stream()
				.filter(icon -> icon.getWidth() == size.width && icon.getHeight() == size.height && icon.getBitCount() == bpp)
				.map(resource::getImage)
				.findFirst();
	}
}
