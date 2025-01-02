package us.irmak.win32.iconexplorer;

import static java.util.Arrays.asList;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultTreeModel;

import com.formdev.flatlaf.FlatLightLaf;
import com.sun.jna.platform.win32.Ole32;

import us.irmak.win32.iconexplorer.IconExplorerModel.IconExplorerModelProperty;

public class IconExplorerUI {
	
	private JFrame frame;
	private JTable table;
	private JTree tree;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JLabel rightStatusLabel;
	private CardLayout rightStatusCardPanelLayout;
	private JPanel statusBarContainer;
	private JPanel rightStatusCardPanel;
	private JTabbedPane tabContainerPane;
	
	private Action openAction = new OpenAction(this);
	private Action exportAction = new ExportAction(this);
	
	private IconExplorerModel model = new IconExplorerModel(new PropertyChangeManager());
	private ProgressMonitor progressMonitor = new ProgressMonitor();
	private FileIconTableModel tableModel = new FileIconTableModel();
	private TreeSelectionListener treeSelectionListener = new TreeSelectionManager(model, progressMonitor, tableModel);
	private FilterModel filterModel = new FilterModel();
	private FilterItemChangeListener filterChangeListener = new FilterItemChangeListener();
	
	private DefaultComboBoxModel<FilterItem<Dimension>> dimensionComboBoxModel = new DefaultComboBoxModel<>();
	private DefaultComboBoxModel<FilterItem<Short>> bppsComboBoxModel = new DefaultComboBoxModel<>();
	
	private List<ResourceSummary> resourceSummaryList = new ArrayList<>();

	
	private static final IconManager ICON_MANAGER = new IconManager();
	private JLabel fileInformationLabel;
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		SwingUtilities.invokeLater(() -> {
			try {
				Ole32.INSTANCE.CoInitialize(null);
				UIManager.setLookAndFeel(new FlatLightLaf());
				//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				IconExplorerUI ui = new IconExplorerUI();
				ui.initialize();
				//ui.frame.setIconImages(ICON_MANAGER.getResourceIcon(new File("C:\\Windows\\system32\\imageres.dll"), "1003").getResolutionVariants());
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				ui.frame.setIconImages(asList(toolkit.getImage("c:\\Users\\irmfatih\\Downloads\\picture.png"),
										toolkit.getImage("c:\\Users\\irmfatih\\Downloads\\picture(1).png"),
										toolkit.getImage("c:\\Users\\irmfatih\\Downloads\\picture(2).png"),
										toolkit.getImage("c:\\Users\\irmfatih\\Downloads\\picture(3).png"),
										toolkit.getImage("c:\\Users\\irmfatih\\Downloads\\picture(4).png")
						 ));
				ui.frame.setTitle("Icon Explorer");
				System.out.format("Start up time:%d\n", System.currentTimeMillis()-start);
				ui.frame.setVisible(true);
				Ole32.INSTANCE.CoUninitialize();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		});
	}
	
	public JFrame getWindowFrame() {
		return frame;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1470, 876);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu menuFile = new JMenu("File");
		menuBar.add(menuFile);
		
		JMenuItem menuItemOpen = new JMenuItem("Open");
		menuItemOpen.setAction(openAction);
		menuFile.add(menuItemOpen);
		
		JMenuItem menuItemExport = new JMenuItem("Export All");
		menuItemExport.setAction(exportAction);
		menuFile.add(menuItemExport);
		
		JMenuItem menuItemExportSelected = new JMenuItem("Export Selected");
		menuItemExportSelected.setEnabled(false);
		menuFile.add(menuItemExportSelected);
		
		JMenu menuRecent = new JMenu("Recent");
		menuFile.add(menuRecent);
		
		JSeparator separator_1 = new JSeparator();
		menuFile.add(separator_1);
		
		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
		menuFile.add(menuItemExit);
		
		JMenu menuView = new JMenu("View");
		menuBar.add(menuView);
		
		JCheckBoxMenuItem menuItemFolderExplorer = new JCheckBoxMenuItem("Folder Explorer");
		menuItemFolderExplorer.setSelected(true);
		menuView.add(menuItemFolderExplorer);
		
		JCheckBoxMenuItem menuItemIconDetails = new JCheckBoxMenuItem("Icon Details");
		menuItemIconDetails.setSelected(true);
		menuView.add(menuItemIconDetails);
		
		JSeparator separator = new JSeparator();
		menuView.add(separator);
		
		JMenu menuTheme = new JMenu("Theme");
		menuView.add(menuTheme);
		
		JRadioButtonMenuItem menuItemLightTheme = new JRadioButtonMenuItem("Light");
		menuItemLightTheme.setSelected(true);
		menuTheme.add(menuItemLightTheme);
		
		JRadioButtonMenuItem menuItemDarkTheme = new JRadioButtonMenuItem("Dark");
		menuTheme.add(menuItemDarkTheme);
		
		JRadioButtonMenuItem menuItemSystemTheme = new JRadioButtonMenuItem("System");
		menuTheme.add(menuItemSystemTheme);
		
		JSplitPane mainSplitPane = new JSplitPane();
		mainSplitPane.setResizeWeight(0.2);
		frame.getContentPane().add(mainSplitPane, BorderLayout.CENTER);
		
		JSplitPane explorerSplitPane = new JSplitPane();
		explorerSplitPane.setResizeWeight(0.7);
		explorerSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainSplitPane.setLeftComponent(explorerSplitPane);
		
		JPanel folderInfoPanel = new JPanel();
		explorerSplitPane.setRightComponent(folderInfoPanel);
		folderInfoPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel fileInformationTitlePanel = new JPanel();
		folderInfoPanel.add(fileInformationTitlePanel, BorderLayout.NORTH);
		fileInformationTitlePanel.setLayout(new BorderLayout(0, 0));
		
		fileInformationLabel = new JLabel("Selected Folder") {
			@Override
			public void setText(String text) {
				if (getFont() != null) {
					int width = getSize().width - getInsets().left - getInsets().right;
					FontMetrics metrics = getFontMetrics(getFont());
					int stringWidth = metrics.stringWidth(text);
					if (stringWidth > width) {
						Path path = Path.of(text);
						String root = path.getRoot().toString();
						String name = path.getFileName().toString();
						if (metrics.stringWidth(root + name + "...") > width) {
							text = name;
						} else {
							String value = name;
							Path lastParent = path.getParent();
							while (lastParent != null && metrics.stringWidth(root + "..." + File.separator + lastParent.getFileName().toString()+ File.separator + value) < width) {
								value = lastParent.getFileName() + File.separator + value;
								lastParent = lastParent.getParent();
							}
							text = root + "..." + File.separator + value;
						}
					}
				}
				super.setText(text);
			}
		};
		fileInformationTitlePanel.add(fileInformationLabel);
		
		JScrollPane fileListTableScrollPane = new JScrollPane();
		folderInfoPanel.add(fileListTableScrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(40);
		table.getColumnModel().getColumn(0).setCellRenderer(new TableFileColumnCellRenderer());
		table.getColumnModel().getColumn(1).setPreferredWidth(40);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
		sorter.setComparator(1, (x, y) -> Integer.compare((Integer) y, (Integer) x));
		sorter.toggleSortOrder(1);
		table.setRowSorter(sorter);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int row = table.getSelectedRow();
					if (row != -1) {
						File file = (File) table.getValueAt(row, 0);
						if (file != null) {
							JPanel tab = addNewTab(file.getName(), new VariantImageIcon(ICON_MANAGER.getShellIcon(file), 16, 16));
							FileReadTask task = new FileReadTask(filterModel, tab, progressMonitor, file);
							task.execute();
							progressMonitor.start();
							task.addPropertyChangeListener(event -> {
								if (event.getNewValue() == StateValue.DONE) {
									tabContainerPane.setSelectedComponent(tab.getParent().getParent());
								}
							});
						}
					}
				}
			}
		});
		
		fileListTableScrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		explorerSplitPane.setLeftComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JPanel fileDiscoveryTitlePanel = new JPanel();
		panel.add(fileDiscoveryTitlePanel, BorderLayout.NORTH);
		fileDiscoveryTitlePanel.setLayout(new BorderLayout(5, 0));
		
		JLabel fileDiscoveryLabel = new JLabel("File Discovery");
		fileDiscoveryTitlePanel.add(fileDiscoveryLabel);
		
		JToolBar fileDiscoveryViewToolBar = new JToolBar();
		fileDiscoveryViewToolBar.setFloatable(false);
		fileDiscoveryTitlePanel.add(fileDiscoveryViewToolBar, BorderLayout.EAST);
		
		JToggleButton tglbtnNewToggleButton = new JToggleButton("");
		tglbtnNewToggleButton.setToolTipText("Show hidden folders");
		tglbtnNewToggleButton.setIcon(new ImageIcon(ICON_MANAGER.getResourceIcon(new File("C:\\Windows\\system32\\imageres.dll"), "10")));
		tglbtnNewToggleButton.setSelectedIcon(new ImageIcon(ICON_MANAGER.getResourceIcon(new File("C:\\Windows\\system32\\imageres.dll"), "9")));
		fileDiscoveryViewToolBar.add(tglbtnNewToggleButton);

		JScrollPane treeScrollPane = new JScrollPane();
		panel.add(treeScrollPane, BorderLayout.CENTER);
		
		tree = new JTree();
		treeScrollPane.setViewportView(tree);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setModel(new DefaultTreeModel(new FileSystemRootNode()));
		tree.setCellRenderer(new FileSystemTreeCellRenderer());
		tree.addTreeWillExpandListener(new FileSystemTreeExpandListener());
		tree.addTreeSelectionListener(treeSelectionListener);
		
		JPanel contentContainer = new JPanel();
		mainSplitPane.setRightComponent(contentContainer);
		contentContainer.setLayout(new BorderLayout(0, 0));
		
		tabContainerPane = new JTabbedPane(JTabbedPane.TOP);
		contentContainer.add(tabContainerPane, BorderLayout.CENTER);
		tabContainerPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabContainerPane.getSelectedIndex();
				if (index != -1) {
					exportAction.setEnabled(true);
					if (resourceSummaryList.size() > index) {
						filterModel.setResourceSummary(resourceSummaryList.get(index));
					}
				}
			}
		});
		
		JToolBar iconsViewToolBar = new JToolBar();
		iconsViewToolBar.setFloatable(false);
		contentContainer.add(iconsViewToolBar, BorderLayout.NORTH);
		
		JButton openButton = new JButton("Open");
		openButton.setAction(openAction);
		iconsViewToolBar.add(openButton);
		
		JButton extractButton = new JButton("Extract");
		extractButton.setAction(exportAction);
		iconsViewToolBar.add(extractButton);
		iconsViewToolBar.addSeparator();
		
		JPanel filterContainerPanel = new JPanel();
		FlowLayout fl_filterContainerPanel = (FlowLayout) filterContainerPanel.getLayout();
		fl_filterContainerPanel.setAlignment(FlowLayout.LEFT);
		iconsViewToolBar.add(filterContainerPanel);
		
		JLabel filtersLabel = new JLabel("Filters:");
		filterContainerPanel.add(filtersLabel);
		
		MultiItemComboBox<Dimension> dimensionComboBox = new MultiItemComboBox<>("Dimension");
		dimensionComboBox.setEnabled(false);
		//dimensionComboBox.setEnabled(false);
		dimensionComboBox.setModel(dimensionComboBoxModel);
		filterContainerPanel.add(dimensionComboBox);
		dimensionComboBox.addItemListener(filterChangeListener);
		
		MultiItemComboBox<Short> bppComboBox = new MultiItemComboBox<>("Bit Depth");
		bppComboBox.setEnabled(false);
		//bppComboBox.setEnabled(false);
		bppComboBox.setModel(bppsComboBoxModel);
		bppComboBox.addItemListener(filterChangeListener);
		filterContainerPanel.add(bppComboBox);
		mainSplitPane.setDividerLocation(300);
		
		statusBarContainer = new JPanel();
		frame.getContentPane().add(statusBarContainer, BorderLayout.SOUTH);
		statusBarContainer.setLayout(new BorderLayout(10, 2));
		
		statusLabel = new JLabel("");
		statusBarContainer.add(statusLabel, BorderLayout.CENTER);
		
		rightStatusCardPanel = new JPanel();
		statusBarContainer.add(rightStatusCardPanel, BorderLayout.EAST);
		rightStatusCardPanelLayout = new CardLayout(10, 0);
		rightStatusCardPanel.setLayout(rightStatusCardPanelLayout);
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setMinimumSize(new Dimension(200, 14));
		rightStatusCardPanel.add(progressBar, "progress");
		
		rightStatusLabel = new JLabel("");
		rightStatusCardPanel.add(rightStatusLabel, "text");
		rightStatusCardPanelLayout.show(rightStatusCardPanel, "text");
	}
	
	public void addTreeSelectionChangeListener(TreeSelectionListener listener) {
		tree.addTreeSelectionListener(listener);
	}
	
	public void clearTable() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::clearTable);
			return;
		}
		int rowCount = tableModel.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
	}
	
	public void addTableRow(String fileName, int count) {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(() -> addTableRow(fileName, count));
			return;
		}
		tableModel.addRow(new Object[]{fileName, count});
	}
	
	public void setStatusMessage(String text) {
		statusLabel.setText(text);
	}
	
	public void setStatusImage(ImageIcon image) {
		statusLabel.setIcon(image);
	}
	
	public void showProgressBar(boolean show) {
		rightStatusCardPanelLayout.show(rightStatusCardPanel, show ? "progress" : "text");
	}
	
	public void setProgress(int value) {
		progressBar.setValue(value);
	}
	
	public JPanel addNewTab(String title, Icon icon) {
		JScrollPane scrollPane = new JScrollPane(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(32);
		tabContainerPane.addTab(title, icon, scrollPane, null);
		tabContainerPane.setTabComponentAt(tabContainerPane.getTabCount()-1, new JLabel(title, icon, JLabel.LEADING));
		JPanel panel = new JPanel();
		TileLayout layout = new TileLayout();
		panel.setLayout(layout);
		scrollPane.setViewportView(panel);
		return panel;
	}
	
	public class ProgressMonitor {
		public void start() {
			rightStatusCardPanelLayout.show(rightStatusCardPanel, "progress");
			progressBar.setValue(0);
		}
		
		public void setProgress(int value) {
			progressBar.setValue(value);
		}
		
		public void setStatusText(String text) {
			statusLabel.setText(text);
		}
		
		public void complete() {
			rightStatusCardPanelLayout.show(rightStatusCardPanel, "text");
		}
		
		public void complete(String message) {
			complete();
			rightStatusLabel.setText(message);
		}
	}
	
	public class FilterModel {
		private static Comparator<Dimension> COMPARATOR = Comparator.comparingInt(d -> (int) d.getWidth());
		public void setResourceSummary(ResourceSummary summary) {
			resourceSummaryList.add(summary);
			setDimensionModel(summary.getSizes().stream().sorted(COMPARATOR).map(this::filterItem).toList());
			setBppModel(summary.getBpps().stream().sorted().map(this::filterItem).toList());
		}
		
		private void setDimensionModel(List<FilterItem<Dimension>> filters) {
			dimensionComboBoxModel.removeAllElements();
			dimensionComboBoxModel.addAll(filters);
		}
		
		private void setBppModel(List<FilterItem<Short>> filters) {
			bppsComboBoxModel.removeAllElements();
			bppsComboBoxModel.addAll(filters);
		}
		
		private <T> FilterItem<T> filterItem(T item) {
			return new FilterItem<>(toString(item), item);
		}
		
		private String toString(Object item) {
			if (item instanceof Short) {
				return String.format("%d bpp", item);
			} else if (item instanceof Dimension) {
				Dimension d = (Dimension) item;
				return String.format("%dx%d", d.width, d.height);
			}
			return item == null ? "null" : item.toString();
		}
	}
	
	class PropertyChangeManager implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			IconExplorerModelProperty property = IconExplorerModelProperty.valueOf(evt.getPropertyName());
			switch (property) {
			case SELECTED_FOLDER -> setSelectedFolder(model.getSelectedFolder());
			case RESOURCE_SUMMARY -> resourceSummaryList.add((ResourceSummary) evt.getNewValue());
			default -> throw new IllegalArgumentException("Unexpected value: " + property);

			}
		}
		
		private void setSelectedFolder(File selectedFolder) {
			fileInformationLabel.setText(model.getSelectedFolder().getAbsolutePath());
		}
	}
}
