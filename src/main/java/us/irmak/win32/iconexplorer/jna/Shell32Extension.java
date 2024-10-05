package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface Shell32Extension extends Shell32 {
	public static Shell32Extension INSTANCE = Native.load("shell32", Shell32Extension.class, W32APIOptions.DEFAULT_OPTIONS);
	
	public static final int SHGFI_USEFILEATTRIBUTES = 0x000000010;
	public static final int SHGFI_ICON = 0x000000100;
	public static final int SHGFI_SMALLICON = 0x000000001;
	public static final int SHGFI_ICONLOCATION = 0x000001000;
	Pointer SHGetFileInfo(String pszPath, int dwFileAttributes, SHFILEINFO psfi, int cbFileInfo, int uFlags);
	HRESULT SHGetStockIconInfo(int siid, int uFlags, SHSTOCKICONINFO psii);
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
	
	/**
	 * typedef struct _SHSTOCKICONINFO {
		  DWORD cbSize;
		  HICON hIcon;
		  int   iSysImageIndex;
		  int   iIcon;
		  WCHAR szPath[MAX_PATH];
		} SHSTOCKICONINFO;
	 */
	@FieldOrder({"cbSize", "hIcon", "iSysImageIndex", "iIcon", "szPath"})
	public static class SHSTOCKICONINFO extends Structure {
		public char[] szPath = new char[WinDef.MAX_PATH];
		public int    cbSize = size();
		public HICON  hIcon;
		public int    iSysImageIndex;
		public int    iIcon;
		
		public String getPath() {
			return new String(szPath);
		}
		

	}
	/*
	enum SHSTOCKICONID {
		  SIID_DOCNOASSOC = 0,
		  SIID_DOCASSOC = 1,
		  SIID_APPLICATION = 2,
		  SIID_FOLDER = 3,
		  SIID_FOLDEROPEN = 4,
		  SIID_DRIVE525 = 5,
		  SIID_DRIVE35 = 6,
		  SIID_DRIVEREMOVE = 7,
		  SIID_DRIVEFIXED = 8,
		  SIID_DRIVENET = 9,
		  SIID_DRIVENETDISABLED = 10,
		  SIID_DRIVECD = 11,
		  SIID_DRIVERAM = 12,
		  SIID_WORLD = 13,
		  SIID_SERVER = 15,
		  SIID_PRINTER = 16,
		  SIID_MYNETWORK = 17,
		  SIID_FIND = 22,
		  SIID_HELP = 23,
		  SIID_SHARE = 28,
		  SIID_LINK = 29,
		  SIID_SLOWFILE = 30,
		  SIID_RECYCLER = 31,
		  SIID_RECYCLERFULL = 32,
		  SIID_MEDIACDAUDIO = 40,
		  SIID_LOCK = 47,
		  SIID_AUTOLIST = 49,
		  SIID_PRINTERNET = 50,
		  SIID_SERVERSHARE = 51,
		  SIID_PRINTERFAX = 52,
		  SIID_PRINTERFAXNET = 53,
		  SIID_PRINTERFILE = 54,
		  SIID_STACK = 55,
		  SIID_MEDIASVCD = 56,
		  SIID_STUFFEDFOLDER = 57,
		  SIID_DRIVEUNKNOWN = 58,
		  SIID_DRIVEDVD = 59,
		  SIID_MEDIADVD = 60,
		  SIID_MEDIADVDRAM = 61,
		  SIID_MEDIADVDRW = 62,
		  SIID_MEDIADVDR = 63,
		  SIID_MEDIADVDROM = 64,
		  SIID_MEDIACDAUDIOPLUS = 65,
		  SIID_MEDIACDRW = 66,
		  SIID_MEDIACDR = 67,
		  SIID_MEDIACDBURN = 68,
		  SIID_MEDIABLANKCD = 69,
		  SIID_MEDIACDROM = 70,
		  SIID_AUDIOFILES = 71,
		  SIID_IMAGEFILES = 72,
		  SIID_VIDEOFILES = 73,
		  SIID_MIXEDFILES = 74,
		  SIID_FOLDERBACK = 75,
		  SIID_FOLDERFRONT = 76,
		  SIID_SHIELD = 77,
		  SIID_WARNING = 78,
		  SIID_INFO = 79,
		  SIID_ERROR = 80,
		  SIID_KEY = 81,
		  SIID_SOFTWARE = 82,
		  SIID_RENAME = 83,
		  SIID_DELETE = 84,
		  SIID_MEDIAAUDIODVD = 85,
		  SIID_MEDIAMOVIEDVD = 86,
		  SIID_MEDIAENHANCEDCD = 87,
		  SIID_MEDIAENHANCEDDVD = 88,
		  SIID_MEDIAHDDVD = 89,
		  SIID_MEDIABLURAY = 90,
		  SIID_MEDIAVCD = 91,
		  SIID_MEDIADVDPLUSR = 92,
		  SIID_MEDIADVDPLUSRW = 93,
		  SIID_DESKTOPPC = 94,
		  SIID_MOBILEPC = 95,
		  SIID_USERS = 96,
		  SIID_MEDIASMARTMEDIA = 97,
		  SIID_MEDIACOMPACTFLASH = 98,
		  SIID_DEVICECELLPHONE = 99,
		  SIID_DEVICECAMERA = 100,
		  SIID_DEVICEVIDEOCAMERA = 101,
		  SIID_DEVICEAUDIOPLAYER = 102,
		  SIID_NETWORKCONNECT = 103,
		  SIID_INTERNET = 104,
		  SIID_ZIPFILE = 105,
		  SIID_SETTINGS = 106,
		  SIID_DRIVEHDDVD = 132,
		  SIID_DRIVEBD = 133,
		  SIID_MEDIAHDDVDROM = 134,
		  SIID_MEDIAHDDVDR = 135,
		  SIID_MEDIAHDDVDRAM = 136,
		  SIID_MEDIABDROM = 137,
		  SIID_MEDIABDR = 138,
		  SIID_MEDIABDRE = 139,
		  SIID_CLUSTEREDDRIVE = 140,
		  SIID_MAX_ICONS = 181
		} ;*/
}
