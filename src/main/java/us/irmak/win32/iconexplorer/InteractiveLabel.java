package us.irmak.win32.iconexplorer;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

public class InteractiveLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	public InteractiveLabel() {
		super();
	}

	public InteractiveLabel(Icon image, int horizontalAlignment) {
		this(null, image, horizontalAlignment);
	}

	public InteractiveLabel(Icon image) {
		this(null, image, LEADING);
	}

	public InteractiveLabel(String text, int horizontalAlignment) {
		this(text, null, horizontalAlignment);
	}

	public InteractiveLabel(String text) {
		this(text, null, LEADING);
	}
	
	public InteractiveLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		addMouseListener(new MouseAdapter() {
			Color old;
			@Override
			public void mouseEntered(MouseEvent e) {
				old = InteractiveLabel.this.getBackground();
				InteractiveLabel.this.setOpaque(true);
				InteractiveLabel.this.setBackground(new Color(0x0, 0x78, 0xD7));
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				InteractiveLabel.this.setOpaque(false);
				InteractiveLabel.this.setBackground(old);
				invalidate();
				repaint();
			}
			
		});
	}

	public void mouseMoved(MouseEvent e) {
		System.out.println("mouse moved");
	}

	
}
