package us.irmak.win32.iconexplorer;

import java.util.EventListener;

public interface ItemSelectionListener<T> extends EventListener {
	public void itemSelectionChanged(ItemSelectionEvent<T> event);
}
