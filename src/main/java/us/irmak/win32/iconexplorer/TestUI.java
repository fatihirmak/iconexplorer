package us.irmak.win32.iconexplorer;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.image.BaseMultiResolutionImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Ole32;

import us.irmak.win32.iconexplorer.jna.Kernel32Ext;

public class TestUI {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TestUI window = new TestUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
	}

	/**
	 * Create the application.
	 */
	public TestUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws Exception 
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		IconManager im = new IconManager();
		BaseMultiResolutionImage img = im.getResourceIcon(new File("C:\\Windows\\system32\\imageres.dll"), "50");
		img.getResolutionVariant(16, 16);
		//ImageIcon ic = new VariantImageIcon(new BaseMultiResolutionImage(Util.toImage(fileInfo.hIcon)), 48, 48);
		//ImageIcon ic = new ImageIcon(Util.toImage(fileInfo.hIcon));
		ImageIcon ic = new ImageIcon(img);
		JLabel label = new JLabel(ic);
		label.setVerticalTextPosition(JLabel.BOTTOM);
		label.setHorizontalTextPosition(JLabel.CENTER);
		label.setText(String.format("%dx%d", ic.getIconWidth(), ic.getIconHeight()));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(label, BorderLayout.CENTER);
		
		Kernel32Ext kernel32 = Kernel32Ext.INSTANCE;
		String location = "c:\\Windows\\System32\\imageres.dll";
		Pointer pwszFileMUIPath = new Memory(256);
		Pointer lptr = new Memory(4);
		lptr.setInt(0, 0);
		
		Pointer sptr = new Memory(4);
		sptr.setInt(0, 256);
		
		Pointer enm = new Memory(8);
		enm.setLong(0, 0);
		
		System.out.println(kernel32.GetFileMUIPath(0x10, location, null, lptr, pwszFileMUIPath, sptr, enm));
		System.out.println(pwszFileMUIPath.getWideString(0));
		
		Ole32.INSTANCE.CoUninitialize();
	}

}
