package us.irmak.win32.iconexplorer;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

public class ResourceSummary {
	private Set<Short> bpps = new HashSet<>();
	private Set<Dimension> sizes = new HashSet<>();
	
	public void addBpps(Set<Short> bpps) {
		this.bpps.addAll(bpps);
	}
	
	public void addSizes(Set<Dimension> sizes) {
		this.sizes.addAll(sizes);
	}
	
	public Set<Short> getBpps() {
		return bpps;
	}
	
	public Set<Dimension> getSizes() {
		return sizes;
	}
	
}
