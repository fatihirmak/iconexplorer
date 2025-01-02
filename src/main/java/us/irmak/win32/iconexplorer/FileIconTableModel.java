package us.irmak.win32.iconexplorer;

import java.io.File;

import javax.swing.table.DefaultTableModel;

public class FileIconTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private static final Class<?>[] COLUMN_CLASSES = {File.class, Integer.class};
	public FileIconTableModel() {
		super(new Object[0][], new String[] {"File Name", "Icons"});
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COLUMN_CLASSES[columnIndex];
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	public void clear() {
		int rows = getRowCount();
		for (int i = rows - 1; i >= 0; i--) {
			removeRow(i);
		}
	}
}
