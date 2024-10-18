package us.irmak.win32.iconexplorer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class IconGroupUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private TitledBorder border;
	
	public IconGroupUI() {
		Font titleFont = new Font(getFont().getName(), 0, 14);
		border = new TitledBorder(null, "Icon", TitledBorder.LEFT, TitledBorder.TOP, titleFont, null);
		setBorder(border);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(this, popupMenu);
		
		JMenuItem exportMenuItem = new JMenuItem("Export Icon Group", ResourceUI.ICONS_EXPORT);
		popupMenu.add(exportMenuItem);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
	
	public void addElement(Icon icon, ImageIcon image) {
		
		String text = String.format("%dx%d %dbpp", icon.getWidth(), icon.getHeight(), icon.getBitCount());
		JLabel label = new JLabel(text, image, JLabel.CENTER);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		label.setVerticalAlignment(JLabel.BOTTOM);
		EmptyBorder border = new EmptyBorder(0, 5, 0, 5);
		label.setBorder(border);
		add(label);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(label, popupMenu);
		
		JMenuItem exportMenuItem = new JMenuItem("Export Icon", ResourceUI.ICONS_EXPORT);
		popupMenu.add(exportMenuItem);
		label.addMouseListener(new MouseAdapter() {
			private Color HOVER = new Color(0x0, 0x78, 0xD7);
			private Color old;
			@Override
			public void mouseEntered(MouseEvent e) {
				old = label.getForeground();
				label.setForeground(HOVER);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				label.setForeground(old);
			}
		});
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
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
