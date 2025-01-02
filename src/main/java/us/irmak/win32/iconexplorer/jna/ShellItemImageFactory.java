package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinNT.HRESULT;
import com.sun.jna.platform.win32.WinUser.SIZE;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.PointerByReference;

public class ShellItemImageFactory extends Unknown implements IShellItemImageFactory{
	
	public ShellItemImageFactory(Pointer pointer) {
		super(pointer);
	}
	
	@Override
	public HRESULT GetImage(SIZE size, int flags, HBITMAP phbm) {
		PointerByReference pbr = new PointerByReference();
		HRESULT res = (HRESULT) _invokeNativeObject(3,
                new Object[]{ getPointer(), size, flags, pbr}, HRESULT.class);
		phbm.setPointer(pbr.getValue());
		return res;
	}

}
