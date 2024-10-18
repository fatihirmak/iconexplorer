package us.irmak.win32.iconexplorer;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class IconTreeRenderer extends DefaultTreeCellRenderer {
	private NativeIconResource iconResource;
	public IconTreeRenderer(NativeIconResource iconResource) {
		this.iconResource = iconResource;
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if (value instanceof IconGroupTreeNode) {
			IconGroup group = (IconGroup) ((IconGroupTreeNode) value).getUserObject();
			group.getIcons().stream().sorted(this::compare).findFirst().ifPresent(icon -> {
				ImageIcon imicon = new ImageIcon(iconResource.getImage(icon));
				setIcon(imicon);
				
			});
		}
		return this;
	}
	
	private int compare(Icon left, Icon right) {
		if (left.getWidth() == right.getWidth()) {
			if (left.getBitCount() == right.getBitCount()) {
				return 0;
			}
			return left.getBitCount() < right.getBitCount() ? -1 : 1;
		}
		return left.getWidth() < right.getWidth() ? -1 : 1;
	}
}
