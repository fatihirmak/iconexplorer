package us.irmak.win32.iconexplorer;

import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_ICON;
import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_SMALLICON;
import static us.irmak.win32.iconexplorer.jna.Shell32Extension.SHGFI_USEFILEATTRIBUTES;

import java.awt.image.BufferedImage;
import java.util.Optional;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HRSRC;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import us.irmak.win32.iconexplorer.jna.Shell32Extension;
import us.irmak.win32.iconexplorer.jna.User32Extension;
import us.irmak.win32.iconexplorer.jna.Shell32Extension.SHFILEINFO;

class Util {
	private static Kernel32 kernel = Kernel32.INSTANCE;
	private static final Shell32Extension shell32 = Shell32Extension.INSTANCE;
	private static final GDI32 gdi32 = GDI32.INSTANCE;
	private static final User32Extension user32 = User32Extension.INSTANCE;
	
	public static HRSRC findResource(HMODULE hmodule, int resourceId, ResourceType type) {
		return kernel.FindResource(hmodule, new Pointer(resourceId), type.getPointer());
	}
	
	public static Pointer loadResource(HMODULE hmodule, HRSRC hrsrc) {
		HANDLE handle = kernel.LoadResource(hmodule, hrsrc);
		Pointer pointer = kernel.LockResource(handle);
		kernel.CloseHandle(hrsrc);
		kernel.CloseHandle(handle);
		return pointer;
	}
	
	public static BufferedImage toImage(WinDef.HICON hicon) {
	    WinDef.HBITMAP bitmapHandle = null;
	    User32 user32 = User32.INSTANCE;
	    GDI32 gdi32 = GDI32.INSTANCE;
	    
	    try {
	        WinGDI.ICONINFO info = new WinGDI.ICONINFO();
	        if (!user32.GetIconInfo(hicon, info))
	            return null;

	        info.read();
	        bitmapHandle = Optional.ofNullable(info.hbmColor).orElse(info.hbmMask);

	        WinGDI.BITMAP bitmap = new WinGDI.BITMAP();
	        if (gdi32.GetObject(bitmapHandle, bitmap.size(), bitmap.getPointer()) > 0) {
	            bitmap.read();

	            int width = bitmap.bmWidth.intValue();
	            int height = bitmap.bmHeight.intValue();

	            final WinDef.HDC deviceContext = user32.GetDC(null);
	            WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();

	            bitmapInfo.bmiHeader.biSize = bitmapInfo.bmiHeader.size();
	            if (gdi32.GetDIBits(deviceContext, bitmapHandle, 0, 0, Pointer.NULL, bitmapInfo,
	                    WinGDI.DIB_RGB_COLORS) == 0)
	                throw new IllegalArgumentException("GetDIBits should not return 0");

	            bitmapInfo.read();

	            Memory pixels = new Memory(bitmapInfo.bmiHeader.biSizeImage);
	            bitmapInfo.bmiHeader.biCompression = WinGDI.BI_RGB;
	            bitmapInfo.bmiHeader.biHeight = -height;

	            if (gdi32.GetDIBits(deviceContext, bitmapHandle, 0, bitmapInfo.bmiHeader.biHeight, pixels, bitmapInfo,
	                    WinGDI.DIB_RGB_COLORS) == 0)
	                throw new IllegalArgumentException("GetDIBits should not return 0");

	            int[] colorArray = pixels.getIntArray(0, width * height);
	            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	            image.setRGB(0, 0, width, height, colorArray, 0, width);

	            return image;
	        }
	    } finally {
	        gdi32.DeleteObject(hicon);
	        Optional.ofNullable(bitmapHandle).ifPresent(gdi32::DeleteObject);
	    }
	    return null;
	}
	
	public static BufferedImage getShellIcon(String extension) {
		return getShellIcon(false, extension);
	}
	
	public static BufferedImage getFolderShellIcon() {
		return getShellIcon(true, null);
	}
	
	private static BufferedImage getShellIcon(boolean isFolder, String extension) {
		SHFILEINFO fileInfo = new SHFILEINFO();
		shell32.SHGetFileInfo("filename."+extension, isFolder ? WinNT.FILE_ATTRIBUTE_DIRECTORY : WinNT.FILE_ATTRIBUTE_NORMAL, 
				fileInfo, fileInfo.size(), 
				SHGFI_USEFILEATTRIBUTES | SHGFI_ICON | SHGFI_SMALLICON);

		HICON hicon = fileInfo.hIcon;
		ICONINFO info = ImageUtils.getIconInfo(hicon);
		try {
			return ImageUtils.createImage(info.hbmColor, null);
		} catch (Exception e) {
			throw e;
		} finally {
			gdi32.DeleteObject(info.hbmColor);
			user32.DestroyIcon(hicon);
		}
	}
}
