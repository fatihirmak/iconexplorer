package us.irmak.win32.iconexplorer;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BaseMultiResolutionImage;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

class OpenAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	IconExplorerUI container;
	File lastOpenFolder;
	File windir = new File(System.getenv("windir"));
	IconManager iconManager = new IconManager();
	
	public OpenAction(IconExplorerUI container) {
		this.container = container;
		putValue(NAME, "Open");
		Toolkit tk = Toolkit.getDefaultToolkit();
		//putValue(SMALL_ICON, new VariantImageIcon(new IconManager().getResourceIcon(new File("C:\\Windows\\SystemResources\\imageres.dll.mun"), "3"), 16, 16));
		putValue(SMALL_ICON, new VariantImageIcon(new BaseMultiResolutionImage(tk.getImage("c:\\Users\\irmfatih\\Downloads\\folder.png"), tk.getImage("c:\\Users\\irmfatih\\Downloads\\folder(1).png")), 16, 16));
		putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		putValue(SHORT_DESCRIPTION, "Opens files");
	}
	
	public void actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser();
		if (lastOpenFolder != null) {
			chooser.setCurrentDirectory(lastOpenFolder);
		} else {
			chooser.setCurrentDirectory(new File(windir, "SystemResources"));
		}
		chooser.setSelectedFile(new File(windir, "SystemResources\\imageres.dll.mun"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Resource Files (*.exe, *.dll, *.mun, *.icl)", "exe", "dll", "mun", "icl"));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Icon Files (*.ico)", "ico"));
		chooser.addChoosableFileFilter(chooser.getAcceptAllFileFilter());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setAcceptAllFileFilterUsed(true);
		
		chooser.setMultiSelectionEnabled(false);
		if (chooser.showOpenDialog(container.getWindowFrame()) == JFileChooser.APPROVE_OPTION) {
			lastOpenFolder = chooser.getSelectedFile().getParentFile();
			File choosen = chooser.getSelectedFile();
			JPanel panel = container.addNewTab(choosen.getName(), new VariantImageIcon(iconManager.getShellIcon(choosen), 16, 16));
			/*FileReadTask task = new FileReadTask(container, panel, choosen);
			task.execute();
			container.showProgressBar(true);*/
		}
	}
}