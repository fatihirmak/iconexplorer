package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface Shlwapi extends com.sun.jna.platform.win32.Shlwapi {
	public static final Shlwapi INSTANCE = Native.load("shlwapi", Shlwapi.class, W32APIOptions.DEFAULT_OPTIONS);
	
	boolean PathMakeSystemFolder(String pszPath);
}
