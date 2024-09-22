package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef.BYTE;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.win32.W32APITypeMapper;

@FieldOrder({"bWidth", "bHeight", "bColorCount", "bReserved", "wPlanes", "wBitCount", "dwBytesInRes", "nId"})
public class GroupIconDirectoryEntry extends Structure {
		
	public BYTE  bWidth;
	public BYTE  bHeight;
	public BYTE  bColorCount;
	public BYTE  bReserved;
	public WORD  wPlanes;
	public WORD  wBitCount;
	public DWORD dwBytesInRes;
	public WORD  nId;
	
	public GroupIconDirectoryEntry() {
		super(W32APITypeMapper.DEFAULT);
		System.out.println("hello");
	}
	
	public GroupIconDirectoryEntry(Pointer pointer) {
		super(pointer, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
        read();
	}
	
	@Override
	protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
		return Math.min(2, super.getNativeAlignment(type, value, isFirstElement));
	}
}
