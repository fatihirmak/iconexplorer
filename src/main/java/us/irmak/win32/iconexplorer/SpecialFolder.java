package us.irmak.win32.iconexplorer;

import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_DISPLAYNAME;
import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_ICON;
import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_ICONLOCATION;
import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_PIDL;
import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_SMALLICON;

import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.File;

import com.sun.jna.platform.win32.Guid.GUID;
import com.sun.jna.platform.win32.KnownFolders;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.ptr.PointerByReference;

import us.irmak.win32.iconexplorer.jna.Shell32Extension;
import us.irmak.win32.iconexplorer.jna.Shell32Extension.SHFILEINFO;

public enum SpecialFolder {
	MY_COMPUTER(KnownFolders.FOLDERID_ComputerFolder), 
	DOCUMENTS(KnownFolders.FOLDERID_Documents), 
	DOWNLOADS(KnownFolders.FOLDERID_Downloads), 
	PICTURES(KnownFolders.FOLDERID_Pictures), 
	DESKTOP(KnownFolders.FOLDERID_Desktop);
	
	private String displayName;
	private String path;
	private BaseMultiResolutionImage icon;
	
	private SpecialFolder(GUID guid) {
		Shell32Extension shell32 = Shell32Extension.INSTANCE;
		
		PointerByReference pidl = new PointerByReference();
		shell32.SHGetKnownFolderIDList(guid, 0, null, pidl);
		SHFILEINFO fileInfo = new SHFILEINFO();
		shell32.SHGetFileInfo(pidl.getValue(), -1, fileInfo, fileInfo.size(), SHGFI_PIDL | SHGFI_DISPLAYNAME);
		
		displayName = fileInfo.getDisplayName();
		
		fileInfo = new SHFILEINFO();
		shell32.SHGetFileInfo(pidl.getValue(), 0, fileInfo, fileInfo.size(), SHGFI_PIDL | SHGFI_ICONLOCATION);
		String location = fileInfo.getDisplayName().trim();
		if (location != null && !location.isBlank()) {
			IconManager iconManager = new IconManager();
			File resourceFile = new File(location);
			String iconId = String.valueOf(Math.abs(fileInfo.iIcon));
			icon = iconManager.getResourceIcon(resourceFile, iconId);
		} else {
			fileInfo = new SHFILEINFO();
			shell32.SHGetFileInfo(pidl.getValue(), 0, fileInfo, fileInfo.size(), SHGFI_PIDL | SHGFI_ICON);
			BufferedImage largeIcon = Util.toImage(fileInfo.hIcon);
			
			fileInfo = new SHFILEINFO();
			shell32.SHGetFileInfo(pidl.getValue(), 0, fileInfo, fileInfo.size(), SHGFI_PIDL | SHGFI_ICON | SHGFI_SMALLICON);
			BufferedImage smallIcon = Util.toImage(fileInfo.hIcon);
			
			icon = new BaseMultiResolutionImage(smallIcon, largeIcon);
		}
		shell32.ILFree(pidl.getValue());
		
		PointerByReference ref = new PointerByReference();
		HRESULT res = shell32.SHGetKnownFolderPath(guid, 0, null, ref);
		if (W32Errors.SUCCEEDED(res)) {
			path = ref.getValue().getWideString(0).trim();
			Ole32.INSTANCE.CoTaskMemFree(ref.getValue());
		}
		//path = Shell32Util.getKnownFolderPath(guid);
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public BaseMultiResolutionImage getIcon() {
		return icon;
	}
	
	public String getPath() {
		return path;
	}
}
