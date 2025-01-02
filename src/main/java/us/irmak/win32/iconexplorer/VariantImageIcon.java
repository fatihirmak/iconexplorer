package us.irmak.win32.iconexplorer;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

public class VariantImageIcon extends ImageIcon {
	private static final long serialVersionUID = 1L;
	
	private BaseMultiResolutionImage image;
	private Map<String, Image> scaledInstances; 
	
	int width;
	int height;
	
	public VariantImageIcon(BaseMultiResolutionImage image, int width, int height) {
		super(image);
		scaledInstances = new HashMap<>();
		this.image = image;
		this.width = width;
		this.height = height;
		
		image.getResolutionVariants().forEach(this::loadImage);
	}
	
	@Override
	public int getIconWidth() {
		return width;
	}
	
	@Override
	public int getIconHeight() {
		return height;
	}
	
	protected Image getScaledInstance(int w, int h) {
		String hashKey = w + "x" + h;
		Image bestVariant = image.getResolutionVariant(w, h);
		if (bestVariant.getHeight(getImageObserver()) != h || bestVariant.getWidth(getImageObserver()) != w) {
			if (scaledInstances.containsKey(hashKey)) {
				return scaledInstances.get(hashKey);
			} else {
				Image scaled = bestVariant.getScaledInstance(w, h, Image.SCALE_SMOOTH);
				loadImage(scaled);
				List<Image> variants = new ArrayList<>(image.getResolutionVariants());
				variants.add(scaled);
				variants = variants.stream().sorted((l, r) -> Integer.compare(l.getWidth(getImageObserver()), r.getWidth(getImageObserver()))).toList();
				image = new BaseMultiResolutionImage(variants.toArray(new Image[variants.size()]));
				return getScaledInstance(w, h);
			}
		} else {
			return bestVariant;
		}
	}
	
	public static BufferedImage convertToBufferedImage(Image image)
	{
	    BufferedImage newImage = new BufferedImage(
	        image.getWidth(null), image.getHeight(null),
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = newImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	    return newImage;
	}
	
	@Override
	public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
		final Graphics2D gr = (Graphics2D) g;
		final AffineTransform t = gr.getTransform();
		final double scalingX = t.getScaleX();
		final double scalingY = t.getScaleY();
		gr.scale(1/scalingX, 1/scalingY);

		final int w = (int) Math.floor(getIconWidth() * scalingX);
		final int h = (int) Math.floor(getIconHeight() * scalingY);

		x = (int) Math.floor(x * scalingX);
		y = (int) Math.floor(y * scalingY);
		
		Image bestSize = getScaledInstance(w, h);
		ImageObserver imageObserver = getImageObserver();
		if (imageObserver == null) {
			imageObserver = c;
		}
		gr.drawImage(bestSize, x, y, w, h, null);
		gr.setTransform(t);
	}
}
