package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.platform.win32.Guid.IID;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinUser.SIZE;
import com.sun.jna.platform.win32.COM.IUnknown;

public interface IShellItemImageFactory  extends IUnknown {
	final static IID IID_IShellItemImageFactory = new IID("bcc18b79-ba16-442f-80c4-8a59c30c463b");
	
	public static final int SIIGBF_RESIZETOFIT = 0x00000000;
	public static final int SIIGBF_BIGGERSIZEOK = 0x00000001;
	public static final int SIIGBF_MEMORYONLY = 0x00000002;
	public static final int SIIGBF_ICONONLY = 0x00000004;
	public static final int SIIGBF_THUMBNAILONLY = 0x00000008;
	public static final int SIIGBF_INCACHEONLY  = 0x00000010;
	public static final int SIIGBF_CROPTOSQUARE = 0x00000020;
	public static final int SIIGBF_WIDETHUMBNAILS = 0x00000040;
	public static final int SIIGBF_ICONBACKGROUND = 0x00000080;
	public static final int SIIGBF_SCALEUP = 0x00000100;
	
	HRESULT GetImage(SIZE size, int flags, HBITMAP phbm);
}
