package us.irmak.win32.iconexplorer;

import static java.util.Arrays.asList;
import static us.irmak.win32.iconexplorer.SpecialFolder.DESKTOP;
import static us.irmak.win32.iconexplorer.SpecialFolder.DOCUMENTS;
import static us.irmak.win32.iconexplorer.SpecialFolder.DOWNLOADS;
import static us.irmak.win32.iconexplorer.SpecialFolder.MY_COMPUTER;
import static us.irmak.win32.iconexplorer.SpecialFolder.PICTURES;

import java.awt.image.BaseMultiResolutionImage;
import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class FileSystemRootNode extends FileSystemTreeNode {
	static final IconManager ICON_MANAGER = new IconManager();
	private static final long serialVersionUID = 1L;
	
	public FileSystemRootNode() {
		add(new SpecialFolderTreeNode(DESKTOP));
		add(new SpecialFolderTreeNode(DOCUMENTS));
		add(new SpecialFolderTreeNode(DOWNLOADS));
		add(new SpecialFolderTreeNode(PICTURES));
		add(new SpecialFolderTreeNode(MY_COMPUTER));
	}
	
	private void addChildNodes(File parent, DefaultMutableTreeNode node) {
		if (parent.isDirectory() && parent.listFiles() != null) {
			asList(parent.listFiles(file -> file.isDirectory() && !file.isHidden())).stream()
			.map(DefaultMutableTreeNode::new)
			.forEach(node::add);
		}
	}

	public BaseMultiResolutionImage getImage() {
		return null;
	}
	
	public String getDisplayName() {
		return null;
	}
	
	@Override
	public File getFolder() {
		return null;
	}
	
	class SpecialFolderTreeNode extends FileSystemTreeNode{
		private static final long serialVersionUID = 1L;
		private SpecialFolder specialFolder;

		public SpecialFolderTreeNode(SpecialFolder specialFolder) {
			this.specialFolder = specialFolder;
			setAllowsChildren(true);
			addChildren();
		}
		@Override
		public BaseMultiResolutionImage getImage() {
			return specialFolder.getIcon();
		}
		@Override
		public String getDisplayName() {
			return specialFolder.getDisplayName();
		}
		@Override
		public File getFolder() {
			if (specialFolder.getPath() != null) {
				return new File(specialFolder.getPath());
			}
			return null;
		}
		@Override
		public void addChildren() {
			if (specialFolder == SpecialFolder.MY_COMPUTER) {
				asList(File.listRoots()).stream().forEach(root -> {
					FolderTreeNode node = new FolderTreeNode(root);
					addChildNodes(root, node);
					add(node);
				});
			} else {
				super.addChildren();
			}
		}
	}
}
