package us.irmak.win32.iconexplorer;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import us.irmak.win32.iconexplorer.jna.Shlwapi;

public class ResourceUI {
	private static final String DESKTOP_INI_CONTENT = 
			  "[.ShellClassInfo]\r\n"
			+ "IconFile=%s\r\n"
			+ "IconIndex=-%s\r\n"
			+ "ConfirmFileOp=0\r\n";
	private static final String TITLE = "Icon Explorer - %s";
	
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
	private JTable table;
	private DefaultTableModel tableModel;
	private JMenu mnRecents;
	private Deque<File> recentFiles = new LinkedList<>();
	private String lastExportFormat;
	
	private JMenuItem mntmExportSame;
	private JSplitPane splitPane;
	private JTree tree;
	
	private DefaultMutableTreeNode rootNode;
	private DefaultTreeModel treeModel;
	private JPanel panelPreview;
	
	static ImageIcon ICONS_FOLDER;
	static ImageIcon ICONS_FILE;
	static ImageIcon ICONS_EXPORT;
	static ImageIcon ICONS_OPEN;
	private static List<Image> ICONS_APP;
	
	private static final File windir = new File(System.getenv("windir"));
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					File iconFile = new File(windir, "SystemResources\\imageres.dll.mun");
					NativeIconResource resource = new NativeIconResource(iconFile);
					MultiResolutionImageToolkit toolkit = new MultiResolutionImageToolkit(resource);
					resource.getIconGroups().stream().filter(gr -> gr.getResourceName().equals("3")).findFirst().ifPresent(gr -> {
						ICONS_FOLDER = new VariantImageIcon(new BaseMultiResolutionImage(toolkit.getImages(gr)), 16, 16);
						System.out.println(gr);
					});
					resource.getIconGroups().stream().filter(gr -> gr.getResourceName().equals("67")).findFirst().ifPresent(gr -> {
						ICONS_FILE = new VariantImageIcon(new BaseMultiResolutionImage(toolkit.getImages(gr)), 16, 16);
					});
					
					resource.getIconGroups().stream().filter(gr -> gr.getResourceName().equals("1010")).findFirst().ifPresent(gr -> {
						ICONS_EXPORT = new VariantImageIcon(new BaseMultiResolutionImage(toolkit.getImages(gr)), 16, 16);
						System.out.println(gr);
					});
					resource.getIconGroups().stream().filter(gr -> gr.getResourceName().equals("1025")).findFirst().ifPresent(gr -> {
						ICONS_OPEN = new VariantImageIcon(new BaseMultiResolutionImage(toolkit.getImages(gr)), 16, 16);
					});
					resource.getIconGroups().stream().filter(gr -> gr.getResourceName().equals("1003")).findFirst().ifPresent(gr -> {
						ICONS_APP = Arrays.asList(toolkit.getImages(gr, 32));
					});
					
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			        GraphicsDevice gd = ge.getDefaultScreenDevice();
			        double scale = gd.getDefaultConfiguration().getDefaultTransform().getScaleX();
			        System.out.println(scale);
			        UIManager.getDefaults().keySet().stream()
			        	.filter(key -> key instanceof String)
			        	.map(key -> (String) key)
			        	.filter(key -> key.endsWith(".font") && !key.contains("Menu")).forEach(key -> {
			        	Object value = UIManager.getDefaults().get(key);
			        	System.out.println(key);
			        	if (value instanceof Font) {
			        		Font font = (Font) value;
			        		System.out.println(key + "=>" + font.getSize());
			        		Font newFont = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * scale));
			        		//UIManager.getDefaults().put(key, newFont);
			        	}
			        });
			        
					ResourceUI window = new ResourceUI();
					//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					//SwingUtilities.updateComponentTreeUI(window.frmIconExplorer);
					window.frmIconExplorer.setIconImages(ICONS_APP);
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
		frmIconExplorer.setBounds(100, 100, 900, 545);
		frmIconExplorer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frmIconExplorer.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		menuItemOpen = new JMenuItem("Open");
		
		menuItemOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (lastOpenFolder != null) {
					chooser.setCurrentDirectory(lastOpenFolder);
				} else {
					chooser.setCurrentDirectory(windir);
				}
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Resources (*.exe, *.dll, *.mun)", "exe", "dll", "mun"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("Icon Files (*.ico)", "ico"));
				chooser.addChoosableFileFilter(chooser.getAcceptAllFileFilter());
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setAcceptAllFileFilterUsed(true);
				
				chooser.setMultiSelectionEnabled(false);
				if (chooser.showOpenDialog(frmIconExplorer) == JFileChooser.APPROVE_OPTION) {
					lastOpenFolder = chooser.getSelectedFile().getParentFile();
					File choosen = chooser.getSelectedFile();
					if (choosen.isDirectory()) {
						discoverFolder(choosen);
					} else if (choosen.isFile()) {
						openResource(choosen);
					}
				}
			}
		});
		menuItemOpen.setMnemonic('O');
		menuItemOpen.setIcon(ICONS_OPEN);
		menuItemOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		mnNewMenu.add(menuItemOpen);
		
		menuItemExport = new JMenuItem("Export");
		menuItemExport.setEnabled(false);
		menuItemExport.setIcon(ICONS_EXPORT);
		menuItemExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				if (lastExportFolder != null) {
					chooser.setCurrentDirectory(lastExportFolder);
				}
				chooser.setApproveButtonText("Export");
				chooser.setDialogTitle("select folder");
				chooser.setDialogType(JFileChooser.SAVE_DIALOG);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setSelectedFile(new File("{f}-{r}-{b}bpp-{w}x{h}.png"));
				chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG (*.png)", "png"));
				if (chooser.showOpenDialog(frmIconExplorer) == JFileChooser.APPROVE_OPTION) {
					File choosen = chooser.getSelectedFile();
					lastExportFolder = choosen.getParentFile();
					while (!lastExportFolder.exists() && lastExportFolder.getParentFile() != null) {
						lastExportFolder = lastExportFolder.getParentFile();
					}
					if (!choosen.getName().endsWith(".png")) {
						choosen = new File(choosen.getParent(), choosen.getName() + ".png");
					}
					export(choosen);
				}
			}
		});
		menuItemExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		mnNewMenu.add(menuItemExport);
		
		mntmExportSame = new JMenuItem("Export same");
		mntmExportSame.setEnabled(false);
		mntmExportSame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export(new File(lastExportFolder, lastExportFormat));
			}
		});
		mnNewMenu.add(mntmExportSame);
		
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
		labelStatusBar.setName("left status bar");
		statusBar.add(labelStatusBar);
		
		statuspanel = new JPanel();
		statusBar.add(statuspanel);
		cl_statuspanel = new CardLayout(0, 0);
		statuspanel.setLayout(cl_statuspanel);
		
		labelStatusRight = new JLabel("");
		//labelStatusRight.setFont(newFont);
		labelStatusBar.setName("right status bar");
		statuspanel.add(labelStatusRight, "status");
		labelStatusRight.setHorizontalAlignment(SwingConstants.RIGHT);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		statuspanel.add(progressBar, "progress");
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.1);
		frmIconExplorer.getContentPane().add(splitPane, BorderLayout.CENTER);
		
		scrollPane = new JScrollPane();
		//frmIconExplorer.getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(32);
		splitPane.setRightComponent(scrollPane);
		
		panel = new JPanel();
		scrollPane.setViewportView(panel);
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		
		rootNode = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(rootNode);
		
		tree = new JTree();
		tree.setModel(treeModel);
		
		tree.setBorder(new EmptyBorder(0, 10, 0, 0));
		
		JScrollPane treeScrollPane = new JScrollPane();
		//frmIconExplorer.getContentPane().add(scrollPane, BorderLayout.CENTER);
		treeScrollPane.getVerticalScrollBar().setUnitIncrement(32);
		//treeScrollPane.setViewportView(tree);
		
		splitPane.setLeftComponent(treeScrollPane);
		
		panelPreview = new JPanel();
		panelPreview.setBackground(Color.WHITE);
		treeScrollPane.setViewportView(panelPreview);
		
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
	
	private File getExistingParent(File file) {
		while (file.getParentFile() != null && !file.exists()) {
			file = file.getParentFile();
		}
		return file;
	}
	
	private void export(File target) {
		new Thread(() -> {
			try {
				NativeIconResource resource = new NativeIconResource(currentFile);
				menuItemExport.setEnabled(true);
				menuItemOpen.setEnabled(false);
				progressBar.setValue(0);
				progressBar.setMaximum(resource.size());
				cl_statuspanel.show(statuspanel, "progress");
				labelStatusBar.setText("Exporting...");
				mntmExportSame.setEnabled(true);
				
				AtomicInteger counter = new AtomicInteger(0);
				
				File original = getExistingParent(target);
				lastExportFormat = original.toPath().relativize(target.toPath()).toString();

				resource.getIconGroups().forEach(group -> {
					group.getIcons().stream().forEach(icon -> {
						String path = target.getAbsolutePath();
						path = path.replaceAll("\\{f\\}", currentFile.getName());
						path = path.replaceAll("\\{r\\}", String.valueOf(group.getResourceName()));
						path = path.replaceAll("\\{b\\}", String.valueOf(icon.getBitCount()));
						path = path.replaceAll("\\{w\\}", String.valueOf(icon.getWidth()));
						path = path.replaceAll("\\{h\\}", String.valueOf(icon.getHeight()));
						
						File location = new File(path);
						location.getParentFile().mkdirs();
						
						Path parent = target.toPath().getParent().normalize();
						int parentToCustomize = -1;
						int count = 0;
						while (parent != null && parent.getFileName() != null) {
							String name = parent.getFileName().toString();
							if (name.contains("{r}") && !name.contains("{b}")
									 && !name.contains("{w}") && !name.contains("{h}")) {
								parentToCustomize = count;
							}
							count++;
							parent = parent.getParent();
						}
						File folderToCustomize = location.getParentFile();
						if (parentToCustomize != -1) {
							for (int i = 0; i < parentToCustomize; i++) {
								folderToCustomize = folderToCustomize.getParentFile();
							}
							String resourceName = group.getResourceName();
							Shlwapi.INSTANCE.PathMakeSystemFolder(folderToCustomize.getAbsolutePath());
							File desktopIni = new File(folderToCustomize, "desktop.ini");
							try {
								String content = String.format(DESKTOP_INI_CONTENT, currentFile.getAbsolutePath(), resourceName);
								Files.writeString(desktopIni.toPath(), content, StandardCharsets.ISO_8859_1, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
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
		if (!file.exists()) {
			JOptionPane.showMessageDialog(frmIconExplorer, String.format("%s doesn't exist.", file.getAbsoluteFile()), "File open error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		frmIconExplorer.setTitle(String.format(TITLE, file.getName()));
		recentFiles.remove(file);
		recentFiles.add(file);
		panel.removeAll();
		cl_statuspanel.show(statuspanel, "progress");
		scrollPane.setViewportView(panel);
		new Thread(() -> {
			AtomicInteger counter = new AtomicInteger(0);
			try {
				panelPreview.removeAll();
				panelPreview.invalidate();
				panelPreview.repaint();
				NativeIconResource resource = new NativeIconResource(file);
				addRecentFile(file);
				menuItemExport.setEnabled(true);
				//labelStatusBar.setIcon(new ImageIcon(Util.getShellIcon(file.getName().substring(file.getName().lastIndexOf('.')))));
				labelStatusBar.setIcon(ICONS_FILE);
				currentFile = file;
				progressBar.setMaximum(resource.size());
				labelStatusBar.setText(file.getName());
				labelStatusRight.setText(resource.size() + " icons");
				MultiResolutionImageToolkit toolkit = new MultiResolutionImageToolkit(resource);
				resource.getIconGroups().forEach(group -> {
					IconGroupUI iconGroupPanel = new IconGroupUI();
					iconGroupPanel.setTitle(String.valueOf(group.getResourceName()));
					Image[] images = toolkit.getImages(group);
					group.getIcons().forEach(i -> {
						try {
							iconGroupPanel.addElement(i, new ImageIcon(resource.getImage(i)));
						} catch (Exception e) {
							JLabel errorLabel = new JLabel(e.getMessage());
							errorLabel.setForeground(Color.RED);
							iconGroupPanel.add(errorLabel);
						}
					});
					iconGroupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
					
					//ImageIcon imicon = new ImageIcon(resource.getImage(icon));
					VariantImageIcon imageIcon = new VariantImageIcon(new BaseMultiResolutionImage(images), 32, 32);
					panelPreview.setLayout(new BoxLayout(panelPreview, BoxLayout.Y_AXIS));
					JLabel label = new JLabel(group.getResourceName(), imageIcon, JLabel.CENTER);
					label.setName("group text");
					label.setVerticalTextPosition(JLabel.BOTTOM);
					label.setHorizontalTextPosition(JLabel.CENTER);
					
					Dimension size = label.getMaximumSize();
					label.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int) (size.height*1.5)));
					size = label.getSize();
					label.setPreferredSize(new Dimension(size.width, 50));
					label.setSize(new Dimension(size.width, 50));
					panelPreview.add(label);
					label.addMouseListener(new MouseAdapter() {
						Color old;
						Color oldForeground;
						@Override
						public void mouseEntered(MouseEvent e) {
							old = label.getBackground();
							oldForeground = label.getForeground();
							label.setOpaque(true);
							label.setBackground(new Color(0x0, 0x78, 0xD7));
							label.setForeground(Color.WHITE);
						}
						@Override
						public void mouseExited(MouseEvent e) {
							label.setOpaque(false);
							label.setBackground(old);
							label.setForeground(oldForeground);
						}
						
						@Override
						public void mouseClicked(MouseEvent e) {
							panel.scrollRectToVisible(iconGroupPanel.getBounds());
						}
					});
					
					
					SwingUtilities.invokeLater(() -> {
						panel.add(iconGroupPanel);
						progressBar.setValue(counter.incrementAndGet());
					});
				});
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(frmIconExplorer, e.getMessage(), "File open error", JOptionPane.ERROR_MESSAGE);
			}
			SwingUtilities.invokeLater(() -> {
				scrollPane.revalidate();
				scrollPane.repaint();
				cl_statuspanel.show(statuspanel, "status");
				tree.setModel(treeModel);
			});
		}).start();
			
	}
	
	private void discoverFolder(File folder) {
		if (folder.equals(currentDiscoveryFolder)) {
			scrollPane.setViewportView(table);
			return;
		}
		frmIconExplorer.setTitle(String.format(TITLE, folder.getName()));
		panelPreview.removeAll();
		panelPreview.invalidate();
		panelPreview.repaint();
		addRecentFile(folder);
		int rowCount = tableModel.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		scrollPane.setViewportView(table);
		cl_statuspanel.show(statuspanel, "progress");
		labelStatusBar.setText(folder.getAbsolutePath());
		labelStatusBar.setIcon(ICONS_FOLDER);
		progressBar.setValue(0);
		currentDiscoveryFolder = folder;
		new Thread(() -> {
			File[] files = folder.listFiles(file -> file.isFile() && file.canRead() && NativeIconResource.isPEFormat(file));
			progressBar.setMaximum(files.length);
			AtomicInteger counter = new AtomicInteger(0);
			Arrays.asList(files).stream().forEach(file -> {
				String fileName = file.getName();
				//int count = WinIconResource.getIconCount(file);
				int count = 0;
				boolean exception = false;
				long start = System.currentTimeMillis();
				try {
					count = new NativeIconResource(file).getIconGroups().size();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					fileName += " - " + e.getMessage();
					exception = true;
				}
				System.out.format("%s -> %d\n", fileName, System.currentTimeMillis()-start);
				if (count > 0 || exception) {
					int c = count;
					String name = fileName;
					SwingUtilities.invokeLater(() -> {
						tableModel.addRow(new Object[]{name, c});
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
		recentFiles.offerFirst(file);
		if (recentFiles.size() > 10) {
			recentFiles.removeLast();
		}
		
		mnRecents.removeAll();
		recentFiles.forEach(f -> {
			JMenuItem item = new JMenuItem(f.getName());
			item.setIcon(f.isFile() ? ICONS_FILE : ICONS_FOLDER);
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
