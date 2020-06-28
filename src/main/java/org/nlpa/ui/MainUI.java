package org.nlpa.ui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.Collection;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.GridBagLayout;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import org.bdp4j.types.Instance;

import java.awt.Dimension;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;

public class MainUI {

	private JFrame frame;
	private File selectedFolder = null;
	private JTextArea leftTA;
	private JTable rightTable;
	/*
	 * private JTextArea rightTA;
	 */

	/**
	 * Launches the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI window = new MainUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creates the application.
	 */
	public MainUI() {
		initialize();
	}

	/**
	 * Opens a file selector and allows to select a folder
	 * 
	 * @return the selected folder if selected, null if not
	 */
	private File showFileSelector() {
		JFileChooser fileSelector = new JFileChooser(".");
		fileSelector.setDialogTitle("Select a folder");
		fileSelector.setMultiSelectionEnabled(true);
		fileSelector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int toRet = fileSelector.showOpenDialog(null);
		if (toRet == JFileChooser.APPROVE_OPTION) {
			return fileSelector.getSelectedFile();
		} else {
			return null;
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame();
		frame.setResizable(false);
		frame.setMaximumSize(new Dimension(1024, 600));
		frame.setMinimumSize(new Dimension(1024, 600));
		frame.setPreferredSize(new Dimension(1024, 600));
		frame.getContentPane().setPreferredSize(new Dimension(1024, 600));
		frame.setBounds(100, 100, 1024, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 1020 };
		gridBagLayout.rowHeights = new int[] { 520, 30 };
		gridBagLayout.columnWeights = new double[] { 1.0 };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0 };
		frame.getContentPane().setLayout(gridBagLayout);

		// Main app panel
		JPanel mainPanel = new JPanel();
		mainPanel.setSize(new Dimension(1024, 560));
		mainPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		frame.getContentPane().add(mainPanel);
		mainPanel.setLayout(new GridLayout(1, 0, 0, 0));
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

		// Left app side for data input and control buttons
		JPanel leftPanel = new JPanel();
		mainPanel.add(leftPanel);
		GridBagLayout gblLeftPanel = new GridBagLayout();
		gblLeftPanel.columnWidths = new int[] { 489, 0 };
		gblLeftPanel.rowHeights = new int[] { 40, 310, 40 };
		gblLeftPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
		gblLeftPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
		leftPanel.setLayout(gblLeftPanel);

		// Left side header
		JPanel leftHeader = new JPanel();
		FlowLayout flowLeftHeader = (FlowLayout) leftHeader.getLayout();
		flowLeftHeader.setAlignment(FlowLayout.LEFT);
		leftHeader.setBackground(SystemColor.control);
		GridBagConstraints gbcLeftHeader = new GridBagConstraints();
		gbcLeftHeader.fill = GridBagConstraints.BOTH;
		gbcLeftHeader.gridx = 0;
		gbcLeftHeader.gridy = 0;
		leftPanel.add(leftHeader, gbcLeftHeader);

		// Left side label
		JLabel leftLabel = new JLabel("Write your own text or select a folder:");
		leftLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		leftLabel.setBackground(Color.WHITE);
		leftLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		leftHeader.add(leftLabel);

		// Left side textArea panel for text data input
		JPanel leftTAPanel = new JPanel();
		leftTAPanel.setBackground(Color.WHITE);
		GridBagConstraints gbcTAPanel = new GridBagConstraints();
		gbcTAPanel.fill = GridBagConstraints.BOTH;
		gbcTAPanel.insets = new Insets(1, 0, 2, 0);
		gbcTAPanel.gridx = 0;
		gbcTAPanel.gridy = 1;
		leftTAPanel.setLayout(new GridLayout(0, 1, 0, 0));
		leftPanel.add(leftTAPanel, gbcTAPanel);

		// Left scrollPane to set a vertical scroll for input textArea if needed
		JScrollPane leftScroll = new JScrollPane();
		leftScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		leftTAPanel.add(leftScroll);

		// TextArea for text data input
		leftTA = new JTextArea();
		leftTA.setFont(new Font("Monospaced", Font.BOLD, 13));
		leftTA.setDisabledTextColor(Color.DARK_GRAY);
		leftTA.setBorder(new EmptyBorder(5, 5, 5, 5));
		leftTA.setLineWrap(true);
		leftScroll.setViewportView(leftTA);

		// Control app button panel in the left side
		JPanel leftBtnPanel = new JPanel();
		leftBtnPanel.setBackground(SystemColor.control);
		GridBagConstraints gbcBtnPanel = new GridBagConstraints();
		gbcBtnPanel.fill = GridBagConstraints.BOTH;
		gbcBtnPanel.gridx = 0;
		gbcBtnPanel.gridy = 2;
		leftPanel.add(leftBtnPanel, gbcBtnPanel);
		leftBtnPanel.setBorder(new EmptyBorder(8, 1, 1, 1));

		// Button to start computing the input data
		JButton startButton = new JButton("Start");
		startButton.setPreferredSize(new Dimension(100, 40));
		startButton.setMinimumSize(new Dimension(100, 40));
		startButton.setMaximumSize(new Dimension(100, 40));
		startButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		startButton.setOpaque(true);
		leftBtnPanel.add(startButton);

		JButton clearButton = new JButton("Clear");
		clearButton.setPreferredSize(new Dimension(100, 40));
		clearButton.setMinimumSize(new Dimension(100, 40));
		clearButton.setMaximumSize(new Dimension(100, 40));
		clearButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		leftBtnPanel.add(clearButton);

		// Button to select data files
		JButton selectFolderButton = new JButton("Select folder");
		selectFolderButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg) {
				selectedFolder = showFileSelector();

				if (selectedFolder != null) {
					StringBuffer folderStr = new StringBuffer("Selected folder to process \n");
					folderStr.append("----------------------- \n");
					folderStr.append(selectedFolder.getPath());
					folderStr.append("\n");
					leftTA.setText(folderStr.toString());
				}
			}
		});
		selectFolderButton.setPreferredSize(new Dimension(120, 40));
		selectFolderButton.setMinimumSize(new Dimension(120, 40));
		selectFolderButton.setMaximumSize(new Dimension(120, 40));
		selectFolderButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		leftBtnPanel.add(selectFolderButton);

		// Right app side for showing results
		JPanel rightPanel = new JPanel();
		mainPanel.add(rightPanel);

		GridBagLayout gblRightPanel = new GridBagLayout();
		gblRightPanel.columnWidths = new int[] { 500, 0 };
		gblRightPanel.rowHeights = new int[] { 40, 310, 40 };
		gblRightPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gblRightPanel.rowWeights = new double[] { 0.0, 0.0, 1.0 };

		rightPanel.setLayout(gblRightPanel);

		// Right header panel
		JPanel rightHeader = new JPanel();
		FlowLayout flowRightHeader = (FlowLayout) rightHeader.getLayout();
		flowRightHeader.setAlignment(FlowLayout.LEFT);
		rightHeader.setBackground(SystemColor.control);
		GridBagConstraints gbcRightHeader = new GridBagConstraints();
		gbcRightHeader.insets = new Insets(0, 0, 1, 0);
		gbcRightHeader.fill = GridBagConstraints.BOTH;
		gbcRightHeader.gridx = 0;
		gbcRightHeader.gridy = 0;
		rightPanel.add(rightHeader, gbcRightHeader);

		// Right label
		JLabel rightLabel = new JLabel("Results: ");
		rightLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		rightLabel.setBackground(Color.WHITE);
		rightLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		rightHeader.add(rightLabel);

		// Right results panel
		JPanel rightResults = new JPanel();
		rightResults.setBackground(SystemColor.control);
		GridBagConstraints gbcRightResultPanel = new GridBagConstraints();
		gbcRightResultPanel.fill = GridBagConstraints.BOTH;
		gbcRightResultPanel.anchor = GridBagConstraints.NORTH;
		gbcRightResultPanel.insets = new Insets(1, 0, 0, 0);
		gbcRightResultPanel.gridx = 0;
		gbcRightResultPanel.gridy = 1;
		rightResults.setLayout(new GridLayout(0, 1, 0, 0));
		rightPanel.add(rightResults, gbcRightResultPanel);

		// Right table for showing results
		rightTable = new JTable();
		rightTable.setBackground(UIManager.getColor("Table.selectionBackground"));
		rightTable.setVisible(false);
		rightTable.setRowSelectionAllowed(false);
		rightTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
		rightTable.setRowHeight(40);
		rightTable.setRowMargin(1);
		rightTable.setRowSelectionAllowed(false);
		rightTable.setFocusable(false);
		rightTable.setColumnSelectionAllowed(false);

		rightResults.add(rightTable);

		// Model for right results table
		rightTable.setModel(new DefaultTableModel(5, 2) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// all cells non editable
				return false;
			}
		});

		rightTable.setValueAt("<html><b>Property</b></html>", 0, 0);
		rightTable.setValueAt("<html><b>Value</b></html>", 0, 1);
		
		
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		rightTable.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);
		rightTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
	
		// Panel for page navigation
		JPanel rightPagination = new JPanel();
		GridBagConstraints gbc_rightPagination = new GridBagConstraints();
		gbc_rightPagination.fill = GridBagConstraints.BOTH;
		gbc_rightPagination.gridx = 0;
		gbc_rightPagination.gridy = 2;
		rightPanel.add(rightPagination, gbc_rightPagination);

		// Notification panel
		JPanel notificationPanel = new JPanel();
		notificationPanel.setBackground(UIManager.getColor("TextArea.inactiveForeground"));
		// notificationPanel.setBorder(new EmptyBorder(1, 5, 5, 5));
		GridBagConstraints gbcNotification = new GridBagConstraints();
		gbcNotification.fill = GridBagConstraints.BOTH;
		gbcNotification.gridx = 0;
		gbcNotification.gridy = 1;
		frame.getContentPane().add(notificationPanel, gbcNotification);
		notificationPanel.setLayout(new FlowLayout());

		// Notification area
		JTextArea notificationTA = new JTextArea();
		notificationTA.setBackground(UIManager.getColor("TextArea.inactiveForeground"));
		notificationTA.setOpaque(false);
		notificationTA.setForeground(Color.BLACK);
		notificationTA.setEnabled(true);
		notificationTA.setEditable(false);
		notificationTA.setAutoscrolls(true);
		notificationTA.setFont(new Font("Monospaced", Font.BOLD, 14));
		notificationPanel.add(notificationTA);

		// Setting of start button
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				notificationTA.setText("Calculating emoji and emoticon polarities");
				StartPipeThread thread = new StartPipeThread(rightPagination, notificationTA, startButton);
				thread.start();
			}
		});

		// Setting of clear button, clears all input and data shown
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				rightTable.setVisible(false);
				leftTA.setEnabled(true);
				leftTA.setText("");
				selectedFolder = null;
				notificationTA.setText("");
				rightPagination.removeAll();
				rightPagination.validate();
				rightPagination.repaint();
			}
		});

		new AppCoreThread(notificationTA, startButton);
	}

	/**
	 * Updates UI to show current instance result values
	 * 
	 * @param instance
	 * @throws UnsupportedEncodingException 
	 */
	public void showInstanceResults(Instance instance) {
		Object emojiPolarityProperty = instance.getProperty("emojiPolarity");
		Object emoticonPolarityProperty = instance.getProperty("emoticonPolarity");
		Object emojiProp = instance.getProperty("emojiTest");
		Object emoticonProp = instance.getProperty("emoticonTest");

		leftTA.setText((String) instance.getData().toString());

		rightTable.setValueAt("EmojiPolarity", 1, 0);
		rightTable.setValueAt("EmoticonPolarity", 2, 0);

		if (emojiPolarityProperty == null || Double.isNaN((double) (emojiPolarityProperty))) {
			rightTable.setValueAt("0.0", 1, 1);
		} else {
			rightTable.setValueAt(emojiPolarityProperty, 1, 1);
		}

		if (emoticonPolarityProperty == null || Double.isNaN((double) (emoticonPolarityProperty))) {
			rightTable.setValueAt("0.0", 2, 1);
		} else {
			rightTable.setValueAt(emoticonPolarityProperty, 2, 1);
		}

		rightTable.setValueAt("EmojiProp", 3, 0);
		rightTable.setValueAt("EmoticonProp", 4, 0);

		if (emojiProp.toString() == null || emojiProp.toString().equals("")) {
			rightTable.setValueAt("None", 3, 1);
		} else {
			rightTable.setValueAt(emojiProp.toString(), 3, 1);
		}
		if (emoticonProp.toString() == null || emoticonProp.toString().equals("")) {
			rightTable.setValueAt("None", 4, 1);
		} else {
			rightTable.setValueAt(emoticonProp.toString(), 4, 1);
		}
	}

	/**
	 * Function to render navigability buttons
	 * 
	 * @param instances       array of instances to iterate
	 * @param paginationPanel panel where the navigation buttons will be rendered
	 */
	public void createPagination(Object[] instances, JPanel paginationPanel) {
		int numInstances = instances.length; // Number of instances to be processed
		// To refresh
		paginationPanel.removeAll();

		// Button to navigate to the previous page (instance)
		JButton btnPreviousPage = new JButton("<");
		paginationPanel.add(btnPreviousPage);

		// Only integer input
		NumberFormatter formatter = new NumberFormatter(NumberFormat.getInstance());
		formatter.setValueClass(Integer.class);
		formatter.setMaximum(numInstances);
		formatter.setMinimum(1);
		formatter.setAllowsInvalid(false);
		// Commit value on keystroke
		formatter.setCommitsOnValidEdit(true);
		JFormattedTextField currentPage = new JFormattedTextField(formatter);
		paginationPanel.add(currentPage);
		// Set default value
		currentPage.setText("1");
		showInstanceResults((Instance) instances[0]);

		// Shows instance result details when changed
		currentPage.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getNewValue() != null) {
					showInstanceResults((Instance) instances[Integer.parseInt(event.getNewValue().toString()) - 1]);
				}
			}
		});

		// Separator
		JLabel separatorLbl = new JLabel("/");
		paginationPanel.add(separatorLbl);

		// Max value label
		JLabel maxPageLbl = new JLabel(String.valueOf(numInstances));
		paginationPanel.add(maxPageLbl);

		// Button to navigate to the next page (instance)
		JButton btnNextPage = new JButton(">");
		paginationPanel.add(btnNextPage);

		// Decreases currentPage value by 1 when pressed
		btnPreviousPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numPage = Integer.parseInt(currentPage.getText());
				currentPage.setText(String.valueOf(--numPage));
			}
		});

		// Increases currentPage value by 1 when pressed
		btnNextPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int numPage = Integer.parseInt(currentPage.getText());
				currentPage.setText(String.valueOf(++numPage));
			}
		});

	}

	public class StartPipeThread extends Thread {
		JPanel paginationPanel;
		JTextArea notificationTA;
		JButton startButton;

		public StartPipeThread(JPanel paginationPanel, JTextArea notificationTA, JButton startButton) {
			this.paginationPanel = paginationPanel;
			this.notificationTA = notificationTA;
			this.startButton = startButton;
		}

		public void run() {
			startPipes();
		}

		public void startPipes() {
			Instance instance = null;
			this.startButton.setEnabled(false);

			try {
				// If no folder was selected
				if (selectedFolder == null) {
					if (leftTA.getText().isEmpty()) {
						notificationTA.setText("Write a text or select a folder to process");
					} else {
						StringBuffer text = new StringBuffer(leftTA.getText());

						instance = AppCore.calculateStringPolarities(text);
						showInstanceResults(instance);
						rightTable.setVisible(true);
						notificationTA.setText("Results ready");
					}
				} else { // If a folder was selected
					AppCore.generateInstances(selectedFolder.getAbsolutePath());
					Collection<Instance> instances = AppCore.calculateFilesPolarities();
					createPagination(instances.toArray(), paginationPanel);
					rightTable.setVisible(true);
					notificationTA.setText("Results ready");
				}

				this.startButton.setEnabled(true);
			} catch (Exception e) {
				notificationTA.setText("Error: " + e.getMessage());
			}
		}
	}

	public class AppCoreThread extends Thread {
		JTextArea notificationTA;
		JButton startButton;

		public AppCoreThread(JTextArea notificationTA, JButton startButton) {
			this.notificationTA = notificationTA;
			this.startButton = startButton;
		}

		public void run() {
			this.startButton.setEnabled(false);
			notificationTA.setText("Getting everything ready...");
			new AppCore();
			notificationTA.setText("Ready to use.");
			this.startButton.setEnabled(true);
		}
	}

}
