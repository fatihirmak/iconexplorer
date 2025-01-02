package us.irmak.win32.iconexplorer;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;

public class FileSystemTreeExpandListener implements TreeWillExpandListener {

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		Object lastPathComponent = event.getPath().getLastPathComponent();
		if (lastPathComponent instanceof FileSystemTreeNode) {
			FileSystemTreeNode node = (FileSystemTreeNode) lastPathComponent;
			TreeNode childNode = node.getFirstChild();
			while (childNode != null) {
				if (childNode instanceof FileSystemTreeNode) {
					((FileSystemTreeNode) childNode).addChildren();
				}
				childNode = node.getChildAfter(childNode);
			}
		}
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
	}

}
