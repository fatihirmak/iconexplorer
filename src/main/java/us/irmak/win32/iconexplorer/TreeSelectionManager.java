package us.irmak.win32.iconexplorer;

import java.io.File;

import javax.swing.SwingWorker.StateValue;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import us.irmak.win32.iconexplorer.IconExplorerUI.ProgressMonitor;

public class TreeSelectionManager implements TreeSelectionListener {
	
	private FolderDiscoveryTask task;
	
	private IconExplorerModel model;
	private ProgressMonitor progressMonitor;
	private FileIconTableModel tableModel;
	
	public TreeSelectionManager(IconExplorerModel model, ProgressMonitor progressMonitor, FileIconTableModel tableModel) {
		this.model = model;
		this.progressMonitor = progressMonitor;
		this.tableModel = tableModel;
	}

	@Override
	public void valueChanged(TreeSelectionEvent event) {
		TreePath path = event.getNewLeadSelectionPath();
		if (path == null) {
			return;
		}
		Object lastPath = event.getNewLeadSelectionPath().getLastPathComponent();
		if (lastPath instanceof FileSystemTreeNode) {
			File file = ((FileSystemTreeNode) lastPath).getFolder();
			if (file != null) {
				model.setSelectedFolder(file);
				if (task != null && !task.isDone()) {
					task.cancel(true);
				}
				task = new FolderDiscoveryTask(tableModel, file);
				task.addPropertyChangeListener(e -> {
					if (e.getNewValue() ==  StateValue.DONE) {
						completed();
					} else if ("progress".equals(e.getPropertyName())) {
						progressUpdate((int) e.getNewValue());
					}
				});
				progressMonitor.start();
				tableModel.clear();
				task.execute();
			}
		}
	}
	
	private void progressUpdate(int progress) {
		progressMonitor.setProgress(progress);
	}
	
	private void completed() {
		progressMonitor.complete();
	}

}
