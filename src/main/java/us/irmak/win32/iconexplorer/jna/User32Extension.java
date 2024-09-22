package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface User32Extension extends User32 {
	public static final int LOAD_LIBRARY_AS_IMAGE_RESOURCE = 0x00000020;
	
	public static final int RT_ICON = 3; // MAKEINTRESOURCE(3)
	public static final int RT_GROUP_ICON = RT_ICON + 11; // MAKEINTRESOURCE((ULONG_PTR)(RT_ICON) + 11) 
	
	User32Extension INSTANCE = Native.load("user32", User32Extension.class, W32APIOptions.DEFAULT_OPTIONS);
	
	int PrivateExtractIcons(String lpszFile, int nIconIndex, int cxIcon, int cyIcon, HICON[] phicon, Pointer piconid, int nIcons, int flags);
	
	HICON CreateIconFromResourceEx(Pointer presbits, int dwResSize, boolean fIcon, int dwVer, int cxDesired, int cyDesired, int flags);
}
