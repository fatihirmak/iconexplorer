package us.irmak.win32.iconexplorer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JLabel;

public class IconTileContainer extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private CellRendererPane cellRendererPane;
	private JLabel cellRenderer;
	
	public IconTileContainer() {
		setLayout(new TileLayout());
		cellRendererPane = new CellRendererPane();
		cellRenderer = new JLabel();
	}
	
	@Override
	public void paint(Graphics g) {
		Rectangle bounds = g.getClipBounds();
		Dimension tileSize = getTileSize(this);
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
}
