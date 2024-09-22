package us.irmak.win32.iconexplorer;

import java.util.Optional;

import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HICON;

public class MonochromeBitmap extends IconBitmap {

	public MonochromeBitmap(HICON hicon) {
		super(hicon);
	}

	@Override
	public HBITMAP getColorImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<HBITMAP> getMask() {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
