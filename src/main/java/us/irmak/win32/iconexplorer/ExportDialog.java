package us.irmak.win32.iconexplorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;

public class ExportDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ExportDialog dialog = new ExportDialog(null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IconResource resource;
	/**
	 * Create the dialog.
	 */
	public ExportDialog(Frame owner, IconResource resource) {
		super(owner, "Export Icons", true);
		this.resource = resource;
		setTitle("Export Icons");
		setSize(632, 464);
		this.setLocationRelativeTo(owner);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(4, 0));
		{
			JSplitPane splitPane = new JSplitPane();
			splitPane.setResizeWeight(0.4);
			contentPanel.add(splitPane, BorderLayout.CENTER);
			{
				JPanel panel = new JPanel();
				splitPane.setLeftComponent(panel);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JPanel panel_1 = new JPanel();
					panel.add(panel_1, BorderLayout.NORTH);
					panel_1.setLayout(new BorderLayout(0, 0));
					{
						JLabel lblNewLabel = new JLabel("Target Folder:");
						panel_1.add(lblNewLabel, BorderLayout.CENTER);
					}
					{
						
						JToolBar toolBar = new JToolBar();
						panel_1.add(toolBar, BorderLayout.EAST);
						toolBar.setFloatable(false);
						{
							JButton btnNewButton = new JButton(new VariantImageIcon(new IconManager().getResourceIcon(new File("C:\\Windows\\SystemResources\\imageres.dll.mun"), "3"), 16, 16));
							btnNewButton.setToolTipText("New Folder");
							btnNewButton.setHorizontalAlignment(SwingConstants.RIGHT);
							toolBar.add(btnNewButton);
						}
					}
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setMinimumSize(new Dimension(200, 23));
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						JTree tree = new JTree();
						tree.setRootVisible(true);
						//tree.setShowsRootHandles(true);
						tree.setModel(new DefaultTreeModel(new FileSystemRootNode()));
						tree.setCellRenderer(new FileSystemTreeCellRenderer());
						tree.addTreeWillExpandListener(new FileSystemTreeExpandListener());
						scrollPane.setViewportView(tree);
					}
				}
			}
			{
				JPanel panel = new JPanel();
				splitPane.setRightComponent(panel);
				panel.setLayout(new BorderLayout(0, 0));
				{
					JPanel panel_1 = new JPanel();
					FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
					flowLayout.setVgap(3);
					flowLayout.setAlignment(FlowLayout.LEFT);
					panel.add(panel_1, BorderLayout.NORTH);
					{
						JLabel lblNewLabel_1 = new JLabel("File Pattern:");
						panel_1.add(lblNewLabel_1);
					}
					{
						JComboBox comboBox = new JComboBox();
						comboBox.setModel(new DefaultComboBoxModel(new String[] {"{file}\\{iconid}\\{width}x{height}-{bpp}bit", "{iconid}\\{width}x{height}-{bpp}bit", "{iconid}\\{width}x{height}\\{bpp}bit", "{iconid}\\{bpp}bit\\{width}x{height}"}));
						comboBox.setEditable(true);
						panel_1.add(comboBox);
					}
					{
						JButton btnNewButton_1 = new JButton("Preview");
						panel_1.add(btnNewButton_1);
					}
				}
				{
					JScrollPane scrollPane = new JScrollPane();
					scrollPane.setViewportBorder(null);
					panel.add(scrollPane, BorderLayout.CENTER);
					{
						JTree tree = new JTree();
						scrollPane.setViewportView(tree);
					}
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Export");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
