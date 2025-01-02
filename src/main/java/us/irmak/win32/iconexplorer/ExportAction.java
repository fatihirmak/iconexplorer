package us.irmak.win32.iconexplorer;

import static java.util.Arrays.asList;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BaseMultiResolutionImage;
import java.io.File;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

class ExportAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private IconExplorerUI ui;
	public ExportAction(IconExplorerUI ui) {
		this.ui = ui;
		putValue(NAME, "Export");
		Toolkit tk = Toolkit.getDefaultToolkit();
		//putValue(SMALL_ICON, new VariantImageIcon(new IconManager().getResourceIcon(new File("C:\\Windows\\SystemResources\\imageres.dll.mun"), "5339"), 16, 16));//1010
		putValue(SMALL_ICON, new VariantImageIcon(new BaseMultiResolutionImage(tk.getImage("c:\\Users\\irmfatih\\Downloads\\arrow.png"), tk.getImage("c:\\Users\\irmfatih\\Downloads\\arrow(1).png")), 16, 16));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		putValue(SHORT_DESCRIPTION, "Exports selected icons into file system");
		setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
		ExportDialog dlg = new ExportDialog(ui.getWindowFrame(), null);
		dlg.setIconImage((Image) new IconManager().getResourceIcon(new File("C:\\Windows\\SystemResources\\imageres.dll.mun"), "5339"));
		dlg.setVisible(true);
	}
}