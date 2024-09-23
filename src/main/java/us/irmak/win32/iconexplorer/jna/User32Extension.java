package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface User32Extension extends User32 {
	public static final int LOAD_LIBRARY_AS_IMAGE_RESOURCE = 0x00000020;
	
	public static final int RT_ICON = 3; // MAKEINTRESOURCE(3)
	public static final int RT_GROUP_ICON = RT_ICON + 11; // MAKEINTRESOURCE((ULONG_PTR)(RT_ICON) + 11) 
	
	User32Extension INSTANCE = Native.load("user32", User32Extension.class, W32APIOptions.DEFAULT_OPTIONS);
	
	HICON CreateIconFromResourceEx(Pointer presbits, int dwResSize, boolean fIcon, int dwVer, int cxDesired, int cyDesired, int flags);
	
	@FieldOrder({"idReserved", "idType", "idCount", "idEntries"})
	public static class GroupIconDirectory extends Structure {
		public WORD idReserved;
		public WORD idType;
		public WORD idCount;
		public GroupIconDirectoryEntry idEntries[] = new GroupIconDirectoryEntry[1];
		
		public GroupIconDirectory() {
			super(W32APITypeMapper.DEFAULT);
		}
		
		public GroupIconDirectory(Pointer pointer) {
			super(pointer, Structure.ALIGN_DEFAULT, W32APITypeMapper.DEFAULT);
			int size = getPointer().getShort(4); // Read dbcp_size (first field in structure)
			if (size > 0) {
		        this.idEntries = new GroupIconDirectoryEntry[size];
		        read();
			} else {
				idEntries = new GroupIconDirectoryEntry[0];
			}
		}
			
	}
	
	@FieldOrder({"bWidth", "bHeight", "bColorCount", "bReserved", "wPlanes", "wBitCount", "dwBytesInRes", "nId"})
	public static class GroupIconDirectoryEntry extends Structure {
			
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
}
