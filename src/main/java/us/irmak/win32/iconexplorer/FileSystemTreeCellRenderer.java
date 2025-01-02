package us.irmak.win32.iconexplorer;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

public class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {
	private static javax.swing.Icon DEFAULT_ICON = UIManager.getIcon("Tree.collapsedIcon");
	
	private static final long serialVersionUID = 1L;
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		
		Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof FileSystemTreeNode) {
			FileSystemTreeNode node = ((FileSystemTreeNode) value);
			setIcon(node.getImage() != null ? new VariantImageIcon(node.getImage(), 16, 16) : DEFAULT_ICON);
			setText(node.getDisplayName());
		}
		
		return component;
	}
}
