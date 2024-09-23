package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface Shell32Extension extends Shell32 {
	public static Shell32Extension INSTANCE = Native.load("shell32", Shell32Extension.class, W32APIOptions.DEFAULT_OPTIONS);
	
	public static final int SHGFI_USEFILEATTRIBUTES = 0x000000010;
	public static final int SHGFI_ICON = 0x000000100;
	public static final int SHGFI_SMALLICON = 0x000000001;
	
	Pointer SHGetFileInfo(String pszPath, int dwFileAttributes, SHFILEINFO psfi, int cbFileInfo, int uFlags);
	
	/**
	 * typedef struct _SHFILEINFOA {
		  HICON hIcon;
		  int   iIcon;
		  DWORD dwAttributes;
		  CHAR  szDisplayName[MAX_PATH];
		  CHAR  szTypeName[80];
		} SHFILEINFOA;
	 */
	@FieldOrder({"hIcon", "iIcon", "dwAttributes", "szDisplayName", "szTypeName"})
	public static class SHFILEINFO extends Structure {
		public HICON hIcon;
		public int   iIcon;
		public int dwAttributes;
		public char[] szDisplayName = new char[WinDef.MAX_PATH];
		public char[] szTypeName = new char[80];
		
		public SHFILEINFO() {
			super(W32APITypeMapper.DEFAULT);
		}
		
		public SHFILEINFO(Pointer pointer) {
			super(pointer, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
	        read();
		}
		
		public String getDisplayName() {
			return new String(szDisplayName);
		}
		
		public String getTypeName() {
			return new String(szTypeName);
		}
	}
}
