package us.irmak.win32.iconexplorer;

import java.util.Optional;

import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HICON;

public abstract class IconBitmap {
	protected HICON hicon;
	
	public IconBitmap(HICON hicon) {
		super();
		this.hicon = hicon;
	}
	public abstract HBITMAP getColorImage();
	public abstract Optional<HBITMAP> getMask();
}
