package us.irmak.win32.iconexplorer;

import java.io.File;

import javax.swing.table.DefaultTableCellRenderer;

public class TableFileColumnCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void setValue(Object value) {
		setText(value == null ? "" : ((File) value).getName());
	}
}
