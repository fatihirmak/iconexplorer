package us.irmak.win32.iconexplorer;

import static us.irmak.win32.iconexplorer.ItemState.DESELECTED;
import static us.irmak.win32.iconexplorer.ItemState.SELECTED;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class MultiItemComboBox<T> extends JComboBox<FilterItem<T>> {

	private static final long serialVersionUID = 1L;
	private List<ItemListener> itemListeners;
	
	public MultiItemComboBox(String title) {
		super.addItemListener(this::itemStateChange);
		FilterItemListRender renderer = new FilterItemListRender(title);
		setRenderer(renderer);
	}
	
	public void setPopupVisible(boolean v) {
	};
	
	private void itemStateChange(ItemEvent event) {
		@SuppressWarnings("unchecked")
		FilterItem<T> item = (FilterItem<T>) event.getItem();
		if (event.getStateChange() == ItemEvent.SELECTED) {
			item.setSelected(!item.isSelected());
			setSelectedIndex(-1);
			ItemSelectionEvent<T> e = new ItemSelectionEvent<T>(this, item, item.isSelected() ? SELECTED : DESELECTED);
			getListeners().forEach(l -> l.itemSelectionChanged(e));
			itemListeners.forEach(l -> l.itemStateChanged(e));
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<ItemSelectionListener<T>> getListeners() {
		return Arrays.asList(listenerList.getListeners(ItemSelectionListener.class));
	}
	
	@Override
	public void setModel(ComboBoxModel<FilterItem<T>> aModel) {
		super.setModel(aModel);
		setSelectedIndex(-1);
	}
	
	public void addItemSelectionListener(ItemSelectionListener<T> listener) {
		listenerList.add(ItemSelectionListener.class, listener);
	}
	
	@Override
	public void addItemListener(ItemListener aListener) {
		if (itemListeners == null) {
			itemListeners = new ArrayList<>();
		}
		itemListeners.add(aListener);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FilterItem<T>[] getSelectedObjects() {
		return IntStream.range(0, getModel().getSize()).boxed()
				.map(getModel()::getElementAt)
				.filter(FilterItem::isSelected)
				.toList().toArray(new FilterItem[0]);
	}
}
