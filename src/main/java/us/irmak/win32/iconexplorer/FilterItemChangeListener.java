package us.irmak.win32.iconexplorer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class FilterItemChangeListener implements ItemListener {

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (!(e.getItem() instanceof FilterItem<?>)) {
			throw new RuntimeException("Expecting a ItemSelectionEvent");
		}
		
	}

}
