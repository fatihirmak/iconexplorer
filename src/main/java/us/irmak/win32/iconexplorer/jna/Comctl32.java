package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Comctl32  extends StdCallLibrary{
	public static Comctl32 INSTANCE = Native.load("comctl32", Comctl32.class, W32APIOptions.DEFAULT_OPTIONS);
	
	int ImageList_GetImageCount(HIMAGELIST himl);
	HICON ImageList_GetIcon(HIMAGELIST himl, int i, int flags);
	boolean ImageList_GetIconSize(HIMAGELIST himl, Pointer x, Pointer y);
	
	public static class HIMAGELIST extends PointerType {
        private boolean immutable;

        public HIMAGELIST() {
        }

        public HIMAGELIST(Pointer p) {
            setPointer(p);
            immutable = true;
        }

        /** Override to the appropriate object for INVALID_HANDLE_VALUE. */
        @Override
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            Object o = super.fromNative(nativeValue, context);
            if (WinBase.INVALID_HANDLE_VALUE.equals(o)) {
                return WinBase.INVALID_HANDLE_VALUE;
            }
            return o;
        }

        @Override
        public void setPointer(Pointer p) {
            if (immutable) {
                throw new UnsupportedOperationException("immutable reference");
            }

            super.setPointer(p);
        }

        @Override
        public String toString() {
            return String.valueOf(getPointer());
        }
    }
}
