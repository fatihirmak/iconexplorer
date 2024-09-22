package us.irmak.win32.iconexplorer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.Closeable;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class ImageDrawer implements Closeable {
	private static User32 user32 = User32.INSTANCE;
	private static GDI32 gdi32 = GDI32.INSTANCE;
	private static Kernel32 kernel32 = Kernel32.INSTANCE;
	
	private static final DirectColorModel SCREENSHOT_COLOR_MODEL = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF, 0xFF000000);
    private static final int[] SCREENSHOT_BAND_MASKS = {
    	SCREENSHOT_COLOR_MODEL.getRedMask(),
        SCREENSHOT_COLOR_MODEL.getGreenMask(),
        SCREENSHOT_COLOR_MODEL.getBlueMask(),
        SCREENSHOT_COLOR_MODEL.getAlphaMask(),
    };
	
    private HDC hdc;
    private WinDef.HDC sourceHdc;
    private WinDef.HDC targetHdc;
    
    public ImageDrawer() {
    	hdc = user32.GetDC(null);
    	sourceHdc = gdi32.CreateCompatibleDC(hdc);
		targetHdc = gdi32.CreateCompatibleDC(hdc);
	}
    
	private int[] getPixels(HICON hicon) {
		HBITMAP hbitmap = null;
		
		try {
			byte[] mask = null;
			ICONINFO iconinfo = getIconInfo(hicon);
			if (iconinfo.hbmColor != null) {
				BITMAPINFO bitmapinfo = getBitmapInfo(hdc, iconinfo.hbmColor);
				if (bitmapinfo.bmiHeader.biBitCount == 32) {
					// icon bitmap is already 32bpp with transparency
					hbitmap = iconinfo.hbmColor;
				} else {
					// icon bitmap doesn't support transparency so create transparent bitmap
					hbitmap = createBitmapFrom(iconinfo.hbmColor, bitmapinfo, 0);
					//mask = getMaskBits(sourceHdc, iconinfo.hbmMask, 0, 0);
				}
			} else if (iconinfo.hbmMask != null) {
				// 1bpp icon only hbmMask field initialized with mask on the top, and the image on the bottom 
				BITMAPINFO bitmapinfo = getBitmapInfo(hdc, iconinfo.hbmMask);
				hbitmap = createBitmapFrom(iconinfo.hbmMask, bitmapinfo, bitmapinfo.bmiHeader.biHeight / 2);
				mask = getMaskBits(sourceHdc, iconinfo.hbmMask, bitmapinfo.bmiHeader.biHeight / 2, bitmapinfo.bmiHeader.biHeight / 2);
			} else {
				throw new RuntimeException("Invalid icon: Both hbmColor and hbmMask are empty.");
			}
			
			BITMAPINFO bitmapinfo = getBitmapInfo(targetHdc, hbitmap);
			System.out.println(bitmapinfo.bmiHeader);
			BITMAPINFOHEADER header = bitmapinfo.bmiHeader;
			int bufferSize = header.biWidth * header.biHeight;
			Memory pixels = getBitmapData(targetHdc, hbitmap, bitmapinfo, 0, header.biHeight);
			
			int[] buffer = pixels.getIntArray(0, bufferSize);
			if (mask != null) {
	    	    for (int i = 0; i < bufferSize; i++) {
	    	    	int pixel = buffer[i];
	    	    	int m = ((mask[i / 8] >> 7-(i % 8) & 0x1) == 0 ? 0xFF : 0x00);
	    	    	buffer[i] = (m << 24) | pixel;
	    	    }
			}
			return buffer;
		} finally {
			if (hbitmap != null) gdi32.DeleteObject(hbitmap);
		}
	}
	
	public BufferedImage createImage(Icon icon, HICON hicon) {
		System.out.println(icon);
		int[] buffer = getPixels(hicon);
		DataBuffer dataBuffer = new DataBufferInt(buffer, buffer.length);
        WritableRaster raster = Raster.createPackedRaster(dataBuffer, icon.getWidth(), icon.getHeight(), icon.getWidth(),
                SCREENSHOT_BAND_MASKS, null);
        return new BufferedImage(SCREENSHOT_COLOR_MODEL, raster, false, null);
	}
	
	private HBITMAP createBitmapFrom(HBITMAP source, BITMAPINFO bitmapinfo, int startY) {
		int width = bitmapinfo.bmiHeader.biWidth;
		int height = bitmapinfo.bmiHeader.biHeight-startY;
		HBITMAP hbitmap = gdi32.CreateCompatibleBitmap(hdc, width, height);
		gdi32.SelectObject(sourceHdc, source);
		gdi32.SelectObject(targetHdc, hbitmap);
		if (!gdi32.BitBlt(targetHdc, 0, 0, width, height, sourceHdc, 0, startY, GDI32.SRCCOPY)) {
    		throw new RuntimeException(String.format("BitBlt failed with error code: ", kernel32.GetLastError()));
    	}
		return hbitmap;
	}
	
	public byte[] getMaskBits(HDC hdc, HBITMAP bitmapHandle, int start, int height) {
		BITMAPINFO bitmapInfo = getBitmapInfo(hdc, bitmapHandle);
		Memory pixels = getBitmapData(hdc, bitmapHandle, bitmapInfo, start, height);
		System.out.println(pixels.dump());
		return pixels.getByteArray(0, bitmapInfo.bmiHeader.biSizeImage);
    }
	
	@Override
	public void close() {
		gdi32.DeleteDC(sourceHdc);
		gdi32.DeleteDC(targetHdc);
		user32.ReleaseDC(null, hdc);
	}
	
	private ICONINFO getIconInfo(HICON hicon) {
		WinGDI.ICONINFO info = new WinGDI.ICONINFO();
        if (!user32.GetIconInfo(hicon, info)) return null;
        info.read();
        return info;
	}
	
	private BITMAPINFO getBitmapInfo(HDC hdc, HBITMAP hbitmap) {
		HANDLE oldObject = gdi32.SelectObject(hdc, hbitmap);
        try {
			WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();
			bitmapInfo.bmiHeader.biSize = bitmapInfo.bmiHeader.size();
			if (gdi32.GetDIBits(hdc, hbitmap, 0, 0, Pointer.NULL, bitmapInfo,
			        WinGDI.DIB_RGB_COLORS) == 0)
			    throw new RuntimeException("GetDIBits failed with error code: " + kernel32.GetLastError());

			bitmapInfo.read();
			return bitmapInfo;
		} finally {
			gdi32.SelectObject(hdc, oldObject);
		}
	}
	
	private Memory getBitmapData(HDC hdc, HBITMAP bitmapHandle, BITMAPINFO bitmapInfo, int start, int height) {
		HANDLE oldObject = gdi32.SelectObject(hdc, bitmapHandle);
		try {
			Memory pixels = new Memory(bitmapInfo.bmiHeader.biSizeImage);
			bitmapInfo.bmiHeader.biHeight = -height;
			bitmapInfo.bmiHeader.biPlanes = 1;
			if (gdi32.GetDIBits(sourceHdc, bitmapHandle, start, height, pixels, bitmapInfo,
					WinGDI.DIB_RGB_COLORS) == 0) {
				throw new RuntimeException(
						String.format("GetDIBits failed with error code: %d", kernel32.GetLastError()));
			}
			return pixels;
		} finally {
			gdi32.SelectObject(hdc, oldObject);
		}
	}
}
