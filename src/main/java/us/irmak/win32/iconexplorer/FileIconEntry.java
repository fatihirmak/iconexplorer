package us.irmak.win32.iconexplorer;

import java.io.File;

public class FileIconEntry {
	File file;
	int count;
	public FileIconEntry(File file, int count) {
		super();
		this.file = file;
		this.count = count;
	}
	public File getFile() {
		return file;
	}
	public int getCount() {
		return count;
	}
}
