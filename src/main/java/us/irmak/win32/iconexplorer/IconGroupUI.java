package us.irmak.win32.iconexplorer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class IconGroupUI extends JPanel {

	private static final long serialVersionUID = 1L;

	private IconResource resource; 
	private TitledBorder border;
	
	public IconGroupUI(IconResource resource) {
		border = new TitledBorder(null, "Icon", TitledBorder.LEFT, TitledBorder.TOP, null, null);
		setBorder(border);
		this.resource = resource;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	public void addElement(Icon icon) {
		BufferedImage image = resource.getImage(icon);
		String text = String.format("%dx%d %dbpp", icon.getWidth(), icon.getHeight(), icon.getBitCount());
		JLabel label = new JLabel(text, new ImageIcon(image), JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		label.setVerticalAlignment(JLabel.BOTTOM);
		EmptyBorder border = new EmptyBorder(0, 5, 0, 5);
		label.setBorder(border);
		add(label);
	}
	
	public void setTitle(String title) {
		border.setTitle(title);
	}
	
	@Override
	public Dimension getMaximumSize() {
		Dimension size = super.getMaximumSize();
		size.width = Integer.MAX_VALUE;
		return size;
	}
}
