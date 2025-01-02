package us.irmak.win32.iconexplorer;

import java.awt.ItemSelectable;
import java.awt.event.ItemEvent;

public class ItemSelectionEvent<T> extends ItemEvent {
	private static final long serialVersionUID = 1L;
	public ItemSelectionEvent(ItemSelectable source, FilterItem<T> item, ItemState state) {
		super(source, ItemEvent.ITEM_STATE_CHANGED, item, state.getState());
	}
	public ItemState getState() {
		return ItemState.of(getStateChange());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public FilterItem<T> getItem() {
		return (FilterItem<T>) super.getItem();
	}
}
