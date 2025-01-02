package us.irmak.win32.iconexplorer;

import static java.util.Arrays.asList;

import java.awt.image.BaseMultiResolutionImage;
import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

public abstract class FileSystemTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;
	private boolean hasExpanded;
	public abstract BaseMultiResolutionImage getImage();
	
	public abstract String getDisplayName();
	
	public abstract File getFolder();
	
	public void addChildren() {
		if (allowsChildren && !hasExpanded) {
			File folder = getFolder();
			removeAllChildren();
			TreeExpandTask task = new TreeExpandTask(folder);
			task.execute();
		}
	}
	
	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), getDisplayName());
	}
	
	class TreeExpandTask extends SwingWorker<Void, FolderTreeNode> {
		private File folder;
		
		public TreeExpandTask(File folder) {
			this.folder = folder;
		}

		@Override
		protected Void doInBackground() throws Exception {
			asList(folder.listFiles(file -> file.isDirectory() && !file.isHidden())).stream()
			.map(FolderTreeNode::new)
			.forEach(this::publish);
			return null;
		}
		
		@Override
		protected void process(List<FolderTreeNode> chunks) {
			chunks.forEach(FileSystemTreeNode.this::add);
		}
		
		@Override
		protected void done() {
			hasExpanded = true;
		}
	}
}
