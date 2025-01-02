package us.irmak.win32.iconexplorer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

public class TileLayout implements LayoutManager {
	
	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int childCount = parent.getComponentCount();
		Dimension prefSize = parent.getSize();
		Dimension minChildSize = getTileSize(parent);
		if (minChildSize.width > prefSize.width) {
			prefSize.width = minChildSize.width;
		}
		int childCountInRow = prefSize.width / minChildSize.width;
		int rowCount = (int) Math.ceil((float) childCount / childCountInRow);
		if (rowCount * minChildSize.height > prefSize.height) {
			prefSize.height = rowCount * minChildSize.height;
		}
		return prefSize;
	}
	
	private Dimension getTileSize(Container parent) {
		int childCount = parent.getComponentCount();
		Dimension minSize = new Dimension(0, 0);
		for (int i = 0; i < childCount; i++) {
			Dimension childMinSize = parent.getComponent(i).getPreferredSize();
			if (childMinSize.getWidth() > minSize.getWidth()) {
				minSize.width = childMinSize.width; 
			}
			if (childMinSize.height > minSize.height) {
				minSize.height = childMinSize.height; 
			}
		}
		if (minSize.width == 0) {
			minSize.width = 1;
		}
		if (minSize.height == 0) {
			minSize.height = 1;
		}
		return minSize;
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		int childCount = parent.getComponentCount();
		Dimension minSize = parent.getMinimumSize();
		for (int i = 0; i < childCount; i++) {
			Dimension childMinSize = parent.getComponent(i).getMinimumSize();
			if (childMinSize.getWidth() > minSize.getWidth()) {
				minSize.width = childMinSize.width; 
			}
			minSize.height += childMinSize.height;
		}
		return minSize;
	}

	@Override
	public void layoutContainer(Container parent) {
		Dimension minChildSize = new Dimension(60, 60);//getTileSize(parent);
		int childCountInRow = preferredLayoutSize(parent).width / minChildSize.width;
		int childCount = parent.getComponentCount();
		for (int i = 0; i < childCount; i++) {
			Component c = parent.getComponent(i);
			int x = (i % childCountInRow) * minChildSize.width;
			int y = (i / childCountInRow) * minChildSize.height;
			c.setLocation(x, y);
			c.setSize(minChildSize);
		}
	}

}
