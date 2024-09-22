package us.irmak.win32.iconexplorer;

import java.util.Optional;

import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;

public class RGBBitmap extends IconBitmap {
	private ICONINFO iconinfo;
	
	public RGBBitmap(HICON hicon) {
		super(hicon);
		iconinfo = ImageUtils.getIconInfo(hicon);
	}

	@Override
	public HBITMAP getColorImage() {
		return iconinfo.hbmColor;
	}

	@Override
	public Optional<HBITMAP> getMask() {
		return Optional.of(iconinfo.hbmMask);
	}

}
