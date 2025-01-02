package us.irmak.win32.iconexplorer;

import static us.irmak.win32.iconexplorer.IconExplorerModel.IconExplorerModelProperty.RESOURCE_SUMMARY;
import static us.irmak.win32.iconexplorer.IconExplorerModel.IconExplorerModelProperty.SELECTED_FOLDER;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IconExplorerModel {
	enum IconExplorerModelProperty {
		RESOURCE_SUMMARY, SELECTED_FOLDER;
	}
	
	private PropertyChangeSupport prop = new PropertyChangeSupport(this);
	
	private List<ResourceSummary> resourceSummaryList = new ArrayList<>();
	private File selectedFolder;
	
	public IconExplorerModel() {
	}
	
	public IconExplorerModel(PropertyChangeListener listener) {
		this();
		addPropertyChangeListener(listener);
	}
	
	public File getSelectedFolder() {
		return selectedFolder;
	}
	public void setSelectedFolder(File selectedFolder) {
		File old = this.selectedFolder;
		this.selectedFolder = selectedFolder;
		prop.firePropertyChange(SELECTED_FOLDER.name(), old, selectedFolder);
	}
	public List<ResourceSummary> getResourceSummaryList() {
		return resourceSummaryList;
	}
	public void addResourceSummary(ResourceSummary rs) {
		resourceSummaryList.add(rs);
		prop.firePropertyChange(RESOURCE_SUMMARY.name(), null, rs);
	}
	public void removeResourceSummary(ResourceSummary rs) {
		resourceSummaryList.remove(rs);
		prop.firePropertyChange(RESOURCE_SUMMARY.name(), rs, null);
	}
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		prop.addPropertyChangeListener(listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		prop.removePropertyChangeListener(listener);
	}
	
	
}
