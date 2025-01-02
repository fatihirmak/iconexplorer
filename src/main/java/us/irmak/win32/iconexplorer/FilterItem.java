package us.irmak.win32.iconexplorer;

public class FilterItem<T> {
	private boolean selected = true;
	private String label;
	private T value;
	
	public FilterItem(String label, T value) {
		super();
		this.label = label;
		this.value = value;
	}
	
	public boolean isSelected() {
		return selected;
	}
	public String getLabel() {
		return label;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public T getValue() {
		return value;
	}
}
