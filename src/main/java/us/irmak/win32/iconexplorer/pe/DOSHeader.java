package us.irmak.win32.iconexplorer.pe;

import java.nio.ByteBuffer;

/**
 * typedef struct _IMAGE_DOS_HEADER 
 */
@FieldOrder({"e_magic", "e_cblp", "e_cp", "e_crlc", "e_cparhdr", "e_minalloc", "e_maxalloc", "e_ss", "e_sp", "e_csum", 
		"e_ip", "e_cs", "e_lfarlc", "e_ovno", "e_res", "e_oemid", "e_oeminfo", "e_res2", "peHeaderOffset"})
public class DOSHeader extends Struct {
	short   e_magic;                     // Magic number: e_magic
    short   e_cblp;                      // Bytes on last page of file
    short   e_cp;                        // Pages in file
    short   e_crlc;                      // Relocations
    short   e_cparhdr;                   // Size of header in paragraphs
    short   e_minalloc;                  // Minimum extra paragraphs needed
    short   e_maxalloc;                  // Maximum extra paragraphs needed
    short   e_ss;                        // Initial (relative) SS value
    short   e_sp;                        // Initial SP value
    short   e_csum;                      // Checksum
    short   e_ip;                        // Initial IP value
    short   e_cs;                        // Initial (relative) CS value
    short   e_lfarlc;                    // File address of relocation table
    short   e_ovno;                      // Overlay number
    short[] e_res = new short[4];        // Reserved words
    short   e_oemid;                     // OEM identifier (for e_oeminfo)
    short   e_oeminfo;                   // OEM information; e_oemid specific
    short[] e_res2 = new short[10];      // Reserved words
    int     peHeaderOffset;                    // File address of new exe header
	
	public DOSHeader() {
		super();
	}
	public DOSHeader(ByteBuffer stream) {
		super(stream);
		readValues();
	}
	
}
