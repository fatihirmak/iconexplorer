package us.irmak.win32.iconexplorer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.tree.DefaultMutableTreeNode;

public class IconGroupTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	public IconGroupTreeNode(IconGroup group) {
		super(group, true);
		Set<Short> bits = group.getIcons().stream().map(Icon::getBitCount).collect(Collectors.toSet());
		List<Short> list = new ArrayList<>(bits);
		list.sort(Comparator.naturalOrder());
		list.forEach(bit -> {
			DefaultMutableTreeNode bitnode = new DefaultMutableTreeNode(bit);
			group.getIcons().stream().filter(icon -> icon.getBitCount() == bit).map(WidthHeight::new).forEach(wh -> {
				DefaultMutableTreeNode dimensionnode = new DefaultMutableTreeNode(wh);
				bitnode.add(dimensionnode);
			});
			
			IconGroupTreeNode.this.add(bitnode);
		});
	}
	
	@Override
	public String toString() {
		if (userObject instanceof IconGroup) {
			return ((IconGroup) userObject).getResourceName();
		}
		return super.toString();
	}
	
	class WidthHeight {
		int width;
		int height;
		public WidthHeight(Icon icon) {
			super();
			this.width = icon.getWidth();
			this.height = icon.getHeight();
		}
		@Override
		public String toString() {
			return width + "x" + height;
		}
	}
}
