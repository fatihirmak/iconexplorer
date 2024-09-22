package us.irmak.win32.iconexplorer;

import java.util.Optional;

import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HICON;

public class ARGBBitmap extends IconBitmap {

	public ARGBBitmap(HICON hicon) {
		super(hicon);
	}

	@Override
	public HBITMAP getColorImage() {
		return ImageUtils.getIconInfo(hicon).hbmColor;
	}

	@Override
	public Optional<HBITMAP> getMask() {
		return Optional.empty();
	}

}
