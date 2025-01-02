package us.irmak.win32.iconexplorer;

import java.awt.event.ItemEvent;

public enum ItemState {
	SELECTED(ItemEvent.SELECTED), DESELECTED(ItemEvent.DESELECTED);
	private int state;

	private ItemState(int state) {
		this.state = state;
	}
	public int getState() {
		return state;
	}
	public static ItemState of(int value) {
		for (ItemState state : values()) {
			if (state.state == value) {
				return state;
			}
		}
		return null;
	}
}
