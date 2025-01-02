package us.irmak.win32.iconexplorer;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import us.irmak.win32.iconexplorer.IconExplorerUI.FilterModel;
import us.irmak.win32.iconexplorer.IconExplorerUI.ProgressMonitor;

public class FileReadTask extends SwingWorker<ResourceSummary, ImageGroup> {
	private File source;
	private FilterModel filterModel;
	private JPanel container;
	private ProgressMonitor monitor;
	
	public FileReadTask(FilterModel filterModel, JPanel container, ProgressMonitor monitor, File source) {
		super();
		this.filterModel = filterModel;
		this.container = container;
		this.source = source;
		this.monitor = monitor;
	}

	@Override
	protected ResourceSummary doInBackground() throws FileNotFoundException {
		if (!source.exists()) {
			throw new IllegalArgumentException(String.format("File %s doesn't exist or not accessible", source));
		}
		ResourceSummary summary = new ResourceSummary();
		try {
			NativeIconResource resource = new NativeIconResource(source);
			float groupCount = resource.getIconGroups().size();
			AtomicInteger counter = new AtomicInteger();
			resource.getIconGroups().forEach(group -> {
				ImageGroup images = new ImageGroup(resource, group);
				summary.addBpps(images.getBPPs());
				summary.addSizes(images.getSizes());
				publish(images);
				setProgress((int) (counter.incrementAndGet() / groupCount * 100));
			});
		} catch (FileNotFoundException e) {
		}
		return summary;
	}
	
	@Override
	protected void done() {
		container.revalidate();
		monitor.complete();
		try {
			ResourceSummary summary = get();
			filterModel.setResourceSummary(summary);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedImage img = new BufferedImage(container.getWidth(), container.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics gr = img.getGraphics();
		container.paint(gr);
		
		try {
			ImageIO.write(img, "png", new File("C:\\Temp\\buffer-out.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void process(List<ImageGroup> chunks) {
		chunks.stream().forEach(ig -> {
			JLabel label = new JLabel(ig.getGroupName(), ig.getImageIcon(), JLabel.CENTER) {
				@Override
				public void paint(Graphics gr) {
					super.paint(gr);
					System.out.println("paint");
				}
			};
			label.setVerticalTextPosition(JLabel.BOTTOM);
			label.setHorizontalTextPosition(JLabel.CENTER);
			container.add(label);
		});
		container.revalidate();
		
		monitor.setProgress(getProgress());
	}

}
