package us.irmak.win32.iconexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingWorker;

public class FolderDiscoveryTask extends SwingWorker<List<String>, FileIconEntry> {
	private FileIconTableModel tableModel;
	private File folder;
	
	public FolderDiscoveryTask(FileIconTableModel tableModel, File folder) {
		super();
		this.tableModel = tableModel;
		this.folder = folder;
	}

	@Override
	protected List<String> doInBackground() throws Exception {
		File[] files = new File[0];
		
		try {
			//files = folder.listFiles(file -> file.isFile() && file.canRead() && NativeIconResource.isPEFormat(file));
			files = folder.listFiles();
		} catch (Exception e) {
		}
		if (files.length <= 0) {
			return Collections.emptyList();
		}
		List<String> failedFiles = new ArrayList<>();
		
		float totalFiles = files.length;
		AtomicInteger counter = new AtomicInteger();
		Arrays.asList(files).stream().parallel().filter(file -> file.isFile() && file.canRead() && NativeIconResource.isPEFormat(file)).forEach(file -> {
			if (isCancelled()) return;
			String fileName = file.getName();
			try {
				int count = new NativeIconResource(file).getIconGroups().size();
				if (count > 0) {
					publish(new FileIconEntry(file, count));
				}
			} catch (Exception e) {
				failedFiles.add(fileName);
			}
			int progress = counter.incrementAndGet();
			setProgress((int) (progress / totalFiles * 100));
		});
		return failedFiles;
	}
	
	@Override
	protected void process(List<FileIconEntry> chunks) {
		chunks.forEach(entry -> {
			tableModel.addRow(new Object[] {entry.getFile(), entry.getCount()});
		});
	}
}
