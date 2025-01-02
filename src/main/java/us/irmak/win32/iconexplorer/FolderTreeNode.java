package us.irmak.win32.iconexplorer;

import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_DISPLAYNAME;

import java.awt.image.BaseMultiResolutionImage;
import java.io.File;

import us.irmak.win32.iconexplorer.jna.Shell32Extension;
import us.irmak.win32.iconexplorer.jna.Shell32Extension.SHFILEINFO;

class FolderTreeNode extends FileSystemTreeNode{
	private static final long serialVersionUID = 1L;
	private File folder;
	
	public FolderTreeNode(File folder) {
		this.folder = folder;
		setAllowsChildren(true);
	}
	@Override
	public BaseMultiResolutionImage getImage() {
		return FileSystemRootNode.ICON_MANAGER.getShellIcon(folder);
	}
	@Override
	public String getDisplayName() {
		if (folder.getName().isBlank()) {
			Shell32Extension shell32 = Shell32Extension.INSTANCE;
			
			SHFILEINFO fileInfo = new SHFILEINFO();
			shell32.SHGetFileInfo(folder.getAbsolutePath(), -1, fileInfo, fileInfo.size(), SHGFI_DISPLAYNAME);
			
			return fileInfo.getDisplayName();
		} else {
			return folder.getName();
		}
	}
	@Override
	public File getFolder() {
		return folder;
	}
}