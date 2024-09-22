package us.irmak.win32.iconexplorer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ResourceUI {

	private JFrame frmIconExplorer;
	private JPanel panel;
	private JScrollPane scrollPane;
	private JLabel labelStatusBar;
	private JLabel labelStatusRight;
	private CardLayout cl_statuspanel;
	private JPanel statuspanel;
	private JProgressBar progressBar;
	
	private File currentFile;
	private JMenuItem menuItemExport;
	private JMenuItem menuItemOpen;
	private File lastOpenFolder;
	private File lastExportFolder;
	private File currentDiscoveryFolder;
	private JMenuItem menuItemOpenFolder;
	private JTable table;
	private DefaultTableModel tableModel;
	private JMenu mnRecents;
	private Stack<File> recentFiles = new Stack<>();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResourceUI window = new ResourceUI();
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SwingUtilities.updateComponentTreeUI(window.frmIconExplorer);
					window.frmIconExplorer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ResourceUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmIconExplorer = new JFrame();
		frmIconExplorer.setTitle("Icon Explorer");
		frmIconExplorer.setBounds(100, 100, 566, 376);
		frmIconExplorer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmIconExplorer.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		menuItemOpen = new JMenuItem("Open File");
		
		menuItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (lastOpenFolder != null) {
					chooser.setCurrentDirectory(lastOpenFolder);
				}
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Resources (*.exe, *.dll)", "exe", "dll"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Icon Files (*.ico)", "ico"));
				chooser.setAcceptAllFileFilterUsed(true);
				chooser.setMultiSelectionEnabled(false);
				if (chooser.showOpenDialog(frmIconExplorer) == JFileChooser.APPROVE_OPTION) {
					lastOpenFolder = chooser.getSelectedFile().getParentFile();
					openResource(chooser.getSelectedFile());
				}
			}
		});
		menuItemOpen.setMnemonic('O');
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnNewMenu.add(menuItemOpen);
		
		menuItemExport = new JMenuItem("Export");
		menuItemExport.setEnabled(false);
		menuItemExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (lastExportFolder != null) {
					chooser.setCurrentDirectory(lastExportFolder);
				}
				chooser.setDialogTitle("select folder");
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setSelectedFile(new File("{f}-{r}-{b}bpp-{w}x{h}.png"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
				if (chooser.showOpenDialog(frmIconExplorer) == JFileChooser.APPROVE_OPTION) {
					File choosen = chooser.getSelectedFile();
					lastExportFolder = choosen.getParentFile();
					if (!choosen.getName().endsWith(".png")) {
						choosen = new File(choosen.getParent(), choosen.getName() + ".png");
					}
					export(choosen);
				}
			}
		});
		
		menuItemOpenFolder = new JMenuItem("Open Folder");
		menuItemOpenFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (lastOpenFolder != null) {
					chooser.setCurrentDirectory(lastOpenFolder);
				}
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(true);
				chooser.setMultiSelectionEnabled(false);
				if (chooser.showOpenDialog(frmIconExplorer) == JFileChooser.APPROVE_OPTION) {
					lastOpenFolder = chooser.getSelectedFile();
					discoverFolder(chooser.getSelectedFile());
				}
			}
		});
		mnNewMenu.add(menuItemOpenFolder);
		menuItemExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		mnNewMenu.add(menuItemExport);
		
		mnNewMenu.addSeparator();
		
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frmIconExplorer.dispose();
			}
		});
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
		mnNewMenu.add(menuItemExit);
		
		mnRecents = new JMenu("Recent Files");
		menuBar.add(mnRecents);
		
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new EmptyBorder(4, 4, 4, 4));
		frmIconExplorer.getContentPane().add(statusBar, BorderLayout.SOUTH);
		statusBar.setLayout(new GridLayout(0, 2, 5, 5));
		
		labelStatusBar = new JLabel("");
		statusBar.add(labelStatusBar);
		
		statuspanel = new JPanel();
		statusBar.add(statuspanel);
		cl_statuspanel = new CardLayout(0, 0);
		statuspanel.setLayout(cl_statuspanel);
		
		labelStatusRight = new JLabel("");
		statuspanel.add(labelStatusRight, "status");
		labelStatusRight.setHorizontalAlignment(SwingConstants.RIGHT);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		statuspanel.add(progressBar, "progress");
		
		scrollPane = new JScrollPane();
		frmIconExplorer.getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(32);
		
		panel = new JPanel();
		scrollPane.setViewportView(panel);
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableModel = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"File", "Icon Count"
				}
			) {
				private static final long serialVersionUID = 1L;
				Class<?>[] columnTypes = new Class[] {
					String.class, Integer.class
				};
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				public boolean isCellEditable(int row, int column) {
					return false;
				}
				
			};
		table.setModel(tableModel);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					if (row != -1) {
						String fileName = (String) table.getValueAt(row, 0);
						if (fileName != null) {
							File resource = new File(currentDiscoveryFolder, fileName);
							if (resource.exists()) {
								openResource(resource);
							}
						}
					}
				}
			}
		});
		table.getColumnModel().getColumn(0).setPreferredWidth(335);
		table.getColumnModel().getColumn(1).setPreferredWidth(280);
		TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
		sorter.setComparator(1, (x, y) -> Integer.compare((Integer) y, (Integer) x));
		sorter.toggleSortOrder(1);
		table.setRowSorter(sorter);
		frmIconExplorer.getContentPane().add(table, BorderLayout.NORTH);
		
		openResource(new File("C:\\Windows\\system32\\user32.dll"));
	}
	
	private void export(File target) {
		new Thread(() -> {
			try (IconResource resource = new IconResource(currentFile)) {
				menuItemExport.setEnabled(true);
				menuItemOpen.setEnabled(false);
				progressBar.setMaximum(resource.size());
				cl_statuspanel.show(statuspanel, "progress");
				labelStatusBar.setText("Exporting...");
				AtomicInteger counter = new AtomicInteger(0);
				resource.getIconGroups().forEach(group -> {
					group.getIcons().stream().forEach(icon -> {
						String path = target.getAbsolutePath();
						path = path.replaceAll("\\{f\\}|\\{filename\\}", currentFile.getName());
						path = path.replaceAll("\\{r\\}|\\{resourceid\\}", String.valueOf(group.getResourceName()));
						path = path.replaceAll("\\{b\\}|\\{bpp\\}", String.valueOf(icon.getBitCount()));
						path = path.replaceAll("\\{w\\}|\\{width\\}", String.valueOf(icon.getWidth()));
						path = path.replaceAll("\\{h\\}|\\{height\\}", String.valueOf(icon.getHeight()));
						
						File location = new File(path);
						location.getParentFile().mkdirs();
						try {
							BufferedImage image = resource.getImage(icon);
							ImageIO.write(image, "png", location);
						} catch (IOException e) {
							labelStatusBar.setText(
									String.format("Failed to save %s.\n%s", location.getAbsoluteFile(), e.getMessage()));
						} catch (RuntimeException e) {
							e.printStackTrace();
							labelStatusBar.setText( 
									String.format("Can't extract icon image:%d\n%s", icon.getResourceId(), e.getMessage()));
						}
						
					});
					
					SwingUtilities.invokeLater(() -> {
						progressBar.setValue(counter.incrementAndGet());
						if (counter.get() == resource.size()) {
							cl_statuspanel.show(statuspanel, "status");
							labelStatusBar.setText("Export completed.");
							menuItemOpen.setEnabled(true);
						}
					});
				});
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frmIconExplorer, 
						String.format("Can't open file: %s\n%s", currentFile, e.getMessage()), 
						"File open error", JOptionPane.ERROR_MESSAGE);
			}
		}).start();
	}
	
	private void openResource(File file) {
		recentFiles.remove(file);
		recentFiles.add(file);
		panel.removeAll();
		cl_statuspanel.show(statuspanel, "progress");
		scrollPane.setViewportView(panel);
		
		new Thread(() -> {
			try (IconResource resource = new IconResource(file)) {
				addRecentFile(file);
				menuItemExport.setEnabled(true);
				currentFile = file;
				progressBar.setMaximum(resource.size());
				labelStatusBar.setText(file.getName());
				labelStatusRight.setText(resource.size() + " icons");
				AtomicInteger counter = new AtomicInteger(0);
				resource.getIconGroups().forEach(group -> {
					IconGroupUI p = new IconGroupUI(resource);
					p.setTitle(String.valueOf(group.getResourceName()));
					group.getIcons().forEach(p::addElement);
					p.setAlignmentX(Component.LEFT_ALIGNMENT);
					SwingUtilities.invokeLater(() -> {
						panel.add(p);
						progressBar.setValue(counter.incrementAndGet());
						scrollPane.revalidate();
						scrollPane.repaint();
						if (counter.get() == resource.size()) {
							cl_statuspanel.show(statuspanel, "status");
						}
					});
				});
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frmIconExplorer, e.getMessage(), "File open error", JOptionPane.ERROR_MESSAGE);
			}
		}).start();
			
	}
	
	private void discoverFolder(File folder) {
		addRecentFile(folder);
		int rowCount = tableModel.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		scrollPane.setViewportView(table);
		cl_statuspanel.show(statuspanel, "progress");
		labelStatusBar.setText(folder.getAbsolutePath());
		currentDiscoveryFolder = folder;
		new Thread(() -> {
			File[] files = folder.listFiles();
			progressBar.setMaximum(files.length);
			AtomicInteger counter = new AtomicInteger(0);
			Arrays.asList(files).stream().forEach(file -> {
				String fileName = file.getName();
				int count = IconResource.getIconCount(file);
				if (count > 0) {
					SwingUtilities.invokeLater(() -> {
						tableModel.addRow(new Object[]{fileName, count});
					});
				}
				progressBar.setValue(counter.incrementAndGet());
				if (counter.get() == files.length) {
					cl_statuspanel.show(statuspanel, "status");
					labelStatusRight.setText("");
				}
			});
		}).start();
	}
	
	private void addRecentFile(File file) {
		recentFiles.remove(file);
		recentFiles.add(file);
		mnRecents.removeAll();
		recentFiles.stream().forEach(f -> {
			JMenuItem item = new JMenuItem(f.getName());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Optional<File> recentFile = recentFiles.stream().filter(f -> f.getName().equals(item.getText())).findFirst();
					recentFile.ifPresent(f -> {
						if (f.isFile()) {
							openResource(f);
						} else if (f.isDirectory()) {
							discoverFolder(f);
						}
						addRecentFile(f);
					});
				}
			});
			mnRecents.add(item);
		});
	}
}
