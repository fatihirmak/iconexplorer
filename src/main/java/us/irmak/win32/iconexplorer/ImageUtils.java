package us.irmak.win32.iconexplorer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;

class ImageUtils {
	private static User32 user32 = User32.INSTANCE;
	private static GDI32 gdi32 = GDI32.INSTANCE;
	private static Kernel32 kernel32 = Kernel32.INSTANCE;
	
    public static BufferedImage createImage(HBITMAP colorBitmap, HBITMAP mask) {
    	if (mask == null && colorBitmap == null) {
    		throw new IllegalArgumentException("Both color bitmap and mask can't be null.");
    	}
    	final WinDef.HDC hdc = user32.GetDC(null);
    	synchronized (hdc) {
	        try {
	        	int[] imageData = null;
	        	int width = 0;
	        	int height = 0;
	        	if (colorBitmap != null) {
			    	BITMAPINFO bitmapInfo = getBitmapInfo(hdc, colorBitmap);
					width = bitmapInfo.bmiHeader.biWidth;
					height = bitmapInfo.bmiHeader.biHeight;
					int imageSize = bitmapInfo.bmiHeader.biSizeImage;
					Memory pixels = getBitmapData(hdc, colorBitmap, bitmapInfo);
					imageData = pixels.getIntArray(0, imageSize / 4);
	        	}
	        	if (mask != null) {
	        		BITMAPINFO maskinfo = getBitmapInfo(hdc, mask);
	        		Memory memory = getBitmapData(hdc, mask, maskinfo);
	        		byte[] maskbits;
					maskbits = memory.getByteArray(0, maskinfo.bmiHeader.biSizeImage);
	        		
	        		if (colorBitmap == null) {
	        			width = maskinfo.bmiHeader.biWidth;
	            		height = -maskinfo.bmiHeader.biHeight / 2;
	            		imageData = new int[width * height];
	            		int start = maskinfo.bmiHeader.biSizeImage / 2;
	            		for (int i = 0; i < maskinfo.bmiHeader.biSizeImage/2; i++) {
	            			for (int j = 0; j < 8; j++) {
	            				imageData[i * 8 + j] = ((maskbits[i+start] >> 7-j) & 0x1) == 0 ? 0x0 : 0xFFFFFF;
	            			}
	            		}
	            		maskbits = Arrays.copyOf(maskbits, maskbits.length/2);
	        		}
	        		
	        		for (int h = 0; h < height; h++) {
	        			for (int w = 0; w < width; w++) {
	        				int pixel = imageData[h * width + w];
	        				int boundry = (int) Math.ceil(width / 32f) * 4;
	        				byte maskbyte = maskbits[h * boundry + w / 8]; //4 byte integer boundry
	        				int maskbit = ((maskbyte >> 7-(w % 8) & 0x1) == 0 ? 0xFF : 0x00);
	        				imageData[h * width + w] = (maskbit << 24) | pixel;
	        			}
	        		}
	        	}
	        	
	        	if (imageData == null) {
	        		throw new RuntimeException("Can't get image data");
	        	}
	        	BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        	image.setRGB(0, 0, width, height, imageData, 0, width);
	        	return image;
	        }  finally {
				user32.ReleaseDC(null, hdc);
			}
    	}
    }
    
    public static BufferedImage createImage(HBITMAP bitmapHandle, int bitCount) {
    	final WinDef.HDC hdc = user32.GetDC(null);
        try {
			BITMAPINFO bitmapInfo = getBitmapInfo(hdc, bitmapHandle);
			int width = bitmapInfo.bmiHeader.biWidth;
			int height = bitmapInfo.bmiHeader.biHeight;
			int imageSize = bitmapInfo.bmiHeader.biSizeImage;
			//bitCount = bitmapInfo.bmiHeader.biBitCount;
			System.out.println(width + "x" + height+" -- " + imageSize);
			Memory pixels = getBitmapData(hdc, bitmapHandle, bitmapInfo);
			BufferedImage image = null;
			if (bitCount == 32) {
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				image.setRGB(0, 0, width, height, pixels.getIntArray(0, imageSize/4), 0, width);
			} else if (bitCount == 1) {
				System.out.println(pixels.dump());
				byte[] data = pixels.getByteArray(0, imageSize);
				for (int h = 0; h < height; h++) {
        			for (int w = 0; w < width; w++) {
        				byte maskbyte = data[h * 4 + w / 8]; //4 bit integer boundry
        				//int maskbit = ((maskbyte >> 7-(w % 8) & 0x1) == 0 ? 0xFF : 0x00);
        				data[h * width + w] = maskbyte;
        			}
        		}
				DataBuffer dataBuffer = new DataBufferByte(data, data.length);
				WritableRaster raster = Raster.createPackedRaster(dataBuffer, width, height, bitCount, null);
				
				image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
				image.setData(raster);
			} else {
				image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				image.setRGB(0, 0, width, height, pixels.getIntArray(0, imageSize/4), 0, width);
			}
			return image;
		} finally {
			user32.ReleaseDC(null, hdc);
		}
    }
    
    public static BITMAPINFO getBitmapInfo(HDC hdc, HBITMAP hbitmap) {
		WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();

		bitmapInfo.bmiHeader.biSize = bitmapInfo.bmiHeader.size();
		if (gdi32.GetDIBits(hdc, hbitmap, 0, 0, Pointer.NULL, bitmapInfo,
		        WinGDI.DIB_RGB_COLORS) == 0)
		    throw new RuntimeException("GetDIBits failed to retrieve info with error: " + Kernel32Util.formatMessage(kernel32.GetLastError()));

		bitmapInfo.read();
		return bitmapInfo;
	}
    
    public static Memory getBitmapData(HDC hdc, HBITMAP bitmapHandle, BITMAPINFO bitmapInfo) {
    	Memory memory = new Memory(bitmapInfo.bmiHeader.biSizeImage);
    	bitmapInfo.bmiHeader.biHeight = -bitmapInfo.bmiHeader.biHeight;
    	bitmapInfo.bmiHeader.biCompression = WinGDI.BI_RGB;
    	//bitmapInfo.bmiHeader.biBitCount = 32;
    	if (gdi32.GetDIBits(hdc, bitmapHandle, 0, bitmapInfo.bmiHeader.biHeight, memory, bitmapInfo, WinGDI.DIB_RGB_COLORS) == 0)
			throw new RuntimeException(
					String.format("GetDIBits failed get data with error: %s", Kernel32Util.formatMessage(kernel32.GetLastError())));
		return memory;
    }
    
    public static ICONINFO getIconInfo(HICON hicon) {
		WinGDI.ICONINFO info = new WinGDI.ICONINFO();
        if (!user32.GetIconInfo(hicon, info)) return null;
        info.read();
        return info;
	}
    
}
