package us.irmak.win32.iconexplorer.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.WinDef.WORD;
import com.sun.jna.win32.W32APITypeMapper;

@FieldOrder({"idReserved", "idType", "idCount", "idEntries"})
public class GroupIconDirectory extends Structure {
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
