package us.irmak.win32.iconexplorer;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class FilterItemListRender implements ListCellRenderer<FilterItem<?>> {
	private JLabel textLabel = new JLabel();
	private JCheckBox checkbox = new JCheckBox();
	private String title;
	
	public FilterItemListRender(String title) {
		super();
		this.title = title;
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends FilterItem<?>> list, FilterItem<?> value, int index,
			boolean isSelected, boolean cellHasFocus) {
		//list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		if (index == -1) {
			textLabel.setText(title);
			return textLabel;
		} else {
			checkbox.setText(value.getLabel());
			checkbox.setSelected(value.isSelected());
			return checkbox;
		}
	}
	

}
