package org.nlpa.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.awt.event.ActionEvent;

import org.nlpa.types.SequenceGroupingStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
import org.nlpa.Main;
import org.nlpa.pipe.impl.*;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import java.awt.SystemColor;
import javax.swing.JTextField;

public class MainUI {

	private JFrame frame;
	private JTable resultsTable;
	private File selectedFolder = null;
	
	
	
	/**
	 * Launch the application.
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
	 * Create the application.
	 */
	public MainUI() {
		initialize();
	}
	
    
    /**
     * Opens a file selector and allow to chose a folder
     * @return the selected folder if selected or null if cancelled
     */
    private File showFileChooser() {
    	JFileChooser fileChooser = new JFileChooser(".");
    	fileChooser.setDialogTitle("Select one or multiple files:");
    	fileChooser.setMultiSelectionEnabled(true);
    	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		else {
			return null;
		}
    }
    
    

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setMaximumSize(new Dimension(1100, 535));
		frame.setMinimumSize(new Dimension(1100, 535));
		frame.setPreferredSize(new Dimension(1100, 535));
		frame.getContentPane().setPreferredSize(new Dimension(1100, 530));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{1094, 0};
		gridBagLayout.rowHeights = new int[] {471, 30, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(1100, 500));
        GridBagConstraints gbc_mainPanel = new GridBagConstraints();
        gbc_mainPanel.insets = new Insets(0, 0, 5, 0);
        gbc_mainPanel.fill = GridBagConstraints.BOTH;
        gbc_mainPanel.gridx = 0;
        gbc_mainPanel.gridy = 0;
        frame.getContentPane().add(mainPanel, gbc_mainPanel);
        mainPanel.setLayout(new GridLayout(1, 0, 0, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 0, 20));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        mainPanel.add(leftPanel);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 10));
        GridBagLayout gbl_leftPanel = new GridBagLayout();
        gbl_leftPanel.columnWidths = new int[]{497, 0};
        gbl_leftPanel.rowHeights = new int[] {50, 290, 50, 0};
        gbl_leftPanel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_leftPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        leftPanel.setLayout(gbl_leftPanel);
        
        JPanel leftHeaderPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) leftHeaderPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        leftHeaderPanel.setPreferredSize(new Dimension(500, 100));
        leftHeaderPanel.setMaximumSize(new Dimension(500, 100));
        leftHeaderPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_leftHeaderPanel = new GridBagConstraints();
        gbc_leftHeaderPanel.fill = GridBagConstraints.BOTH;
        gbc_leftHeaderPanel.insets = new Insets(0, 0, 5, 0);
        gbc_leftHeaderPanel.gridx = 0;
        gbc_leftHeaderPanel.gridy = 0;
        leftPanel.add(leftHeaderPanel, gbc_leftHeaderPanel);
        
        JLabel messageLabel = new JLabel("Test with your own text:");
        messageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        messageLabel.setBackground(Color.WHITE);
        messageLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        leftHeaderPanel.add(messageLabel);
        
        JPanel textPanel = new JPanel();
        textPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_textPanel = new GridBagConstraints();
        gbc_textPanel.fill = GridBagConstraints.BOTH;
        gbc_textPanel.insets = new Insets(0, 0, 5, 0);
        gbc_textPanel.gridx = 0;
        gbc_textPanel.gridy = 1;
        leftPanel.add(textPanel, gbc_textPanel);
        textPanel.setLayout(new GridLayout(0, 1, 0, 0));
        
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setDisabledTextColor(Color.BLACK);
        inputTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputTextArea.setLineWrap(true);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(inputTextArea);
        textPanel.add(scrollPane);
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
        gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
        gbc_buttonsPanel.gridx = 0;
        gbc_buttonsPanel.gridy = 2;
        leftPanel.add(buttonsPanel, gbc_buttonsPanel);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton classifyButton = new JButton("Compute");
        classifyButton.setPreferredSize(new Dimension(105, 23));
        classifyButton.setMinimumSize(new Dimension(105, 23));
        classifyButton.setMaximumSize(new Dimension(105, 23));
        classifyButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        classifyButton.setActionCommand("clasify");
        
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JButton openFilesButton = new JButton("Open files");
        openFilesButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		selectedFolder = showFileChooser();
        		
        		if (selectedFolder != null) {
        			inputTextArea.setEnabled(false);
        			
        			// Information about the selected folder
        			StringBuffer folderInfo = new StringBuffer();
        			folderInfo.append("A folder has been selected to be analized");
        			folderInfo.append(System.getProperty("line.separator"));
        			folderInfo.append("--- --- --- --- --- --- --- --- --- --- --- --- ");
        			folderInfo.append(System.getProperty("line.separator"));
        			folderInfo.append(selectedFolder.getPath());
        			folderInfo.append(System.getProperty("line.separator"));
        			
        			inputTextArea.setText(folderInfo.toString());
        		}
        	}
        });
        openFilesButton.setPreferredSize(new Dimension(105, 23));
        openFilesButton.setMinimumSize(new Dimension(105, 23));
        openFilesButton.setMaximumSize(new Dimension(105, 23));
        openFilesButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonsPanel.add(openFilesButton);
        buttonsPanel.add(classifyButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(105, 23));
        clearButton.setMinimumSize(new Dimension(105, 23));
        clearButton.setMaximumSize(new Dimension(105, 23));
        clearButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        buttonsPanel.add(clearButton);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        mainPanel.add(rightPanel);
        rightPanel.setBorder(new EmptyBorder(20, 10, 20, 20));
        GridBagLayout gbl_rightPanel = new GridBagLayout();
        gbl_rightPanel.columnWidths = new int[]{497, 0};
        gbl_rightPanel.rowHeights = new int[] {50, 290, 50, 0};
        gbl_rightPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_rightPanel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
        rightPanel.setLayout(gbl_rightPanel);
        
        JPanel rightHeaderPanel = new JPanel();
        rightHeaderPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_rightHeaderPanel = new GridBagConstraints();
        gbc_rightHeaderPanel.fill = GridBagConstraints.BOTH;
        gbc_rightHeaderPanel.insets = new Insets(0, 0, 5, 0);
        gbc_rightHeaderPanel.gridx = 0;
        gbc_rightHeaderPanel.gridy = 0;
        rightPanel.add(rightHeaderPanel, gbc_rightHeaderPanel);
        rightHeaderPanel.setLayout(new GridLayout(0, 2, 0, 0));
        
        JPanel resultsTitlePanel = new JPanel();
        resultsTitlePanel.setBackground(Color.WHITE);
        rightHeaderPanel.add(resultsTitlePanel);
        resultsTitlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel resultsLabel = new JLabel("Results:");
        resultsLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        resultsLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        resultsTitlePanel.add(resultsLabel);
        
        JPanel additionalToolsPanel = new JPanel();
        additionalToolsPanel.setBackground(Color.WHITE);
        rightHeaderPanel.add(additionalToolsPanel);
        additionalToolsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JPanel resultsTablePanel = new JPanel();
        resultsTablePanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_resultsTablePanel = new GridBagConstraints();
        gbc_resultsTablePanel.insets = new Insets(0, 0, 5, 0);
        gbc_resultsTablePanel.fill = GridBagConstraints.BOTH;
        gbc_resultsTablePanel.gridx = 0;
        gbc_resultsTablePanel.gridy = 1;
        rightPanel.add(resultsTablePanel, gbc_resultsTablePanel);
        resultsTablePanel.setLayout(new GridLayout(0, 1, 0, 0));
        
        resultsTable = new JTable();
        resultsTable.setVisible(false);
        resultsTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        resultsTable.setShowVerticalLines(false);
        resultsTable.setRowSelectionAllowed(false);
        resultsTable.setRowHeight(50);
        resultsTable.setRowMargin(1);
        resultsTablePanel.add(resultsTable);
        
        // Create table model so the table is displayed
        resultsTable.setModel(new DefaultTableModel(3, 2));
        
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_paginationPanel = new GridBagConstraints();
        gbc_paginationPanel.fill = GridBagConstraints.BOTH;
        gbc_paginationPanel.gridx = 0;
        gbc_paginationPanel.gridy = 2;
        rightPanel.add(paginationPanel, gbc_paginationPanel);
        
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setBorder(new EmptyBorder(0, 20, 5, 20));
        GridBagConstraints gbc_notificationsPanel = new GridBagConstraints();
        gbc_notificationsPanel.fill = GridBagConstraints.BOTH;
        gbc_notificationsPanel.gridx = 0;
        gbc_notificationsPanel.gridy = 1;
        frame.getContentPane().add(notificationsPanel, gbc_notificationsPanel);
        notificationsPanel.setLayout(new GridLayout(0, 1, 0, 0));
        
        JTextArea notificationTextArea = new JTextArea();
        notificationTextArea.setBorder(new EmptyBorder(5, 0, 0, 0));
        notificationTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        notificationTextArea.setDisabledTextColor(SystemColor.desktop);
        notificationTextArea.setBackground(SystemColor.control);
        notificationTextArea.setAutoscrolls(false);
        notificationTextArea.setEnabled(false);
        notificationTextArea.setEditable(false);
        notificationsPanel.add(notificationTextArea);
        
        // Set table headers
        resultsTable.setValueAt("<html><b>PROPERTY</b></html>", 0, 0);
        resultsTable.setValueAt("<html><b>VALUE</b></html>", 0, 1);
        
        // Set right column alignment
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        resultsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        classifyButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		Instance inst = null;
        		
        		if (selectedFolder == null) {
        			StringBuffer text = new StringBuffer(inputTextArea.getText());
            		inst = AppCore.computeStringPolarity(text);
            		
            		showInstanceValues(inst);
        		}
        		else {
        			AppCore.generateInstances(selectedFolder.getAbsolutePath());
        			Collection<Instance> instances = AppCore.computeFilesPolarity();
        			createPagination(instances, paginationPanel);
        		}

                resultsTable.setVisible(true);
                notificationTextArea.setText("The text has been computed");
        	}
        });
        
        clearButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		resultsTable.setVisible(false);
        		inputTextArea.setText("");
        		inputTextArea.setEnabled(true);
        		selectedFolder = null;
        		notificationTextArea.setText("");
        	}
        });
	}
	
	// Update the UI with the values of the instance 
	private void showInstanceValues(Instance instance) {
		Object tokensPolarityProperty = instance.getProperty("polarity");  
        Object synsetsPolarityProperty = instance.getProperty("synsetPolarity");
        
        if (tokensPolarityProperty instanceof Double) {
            resultsTable.setValueAt("Tokens Polarity", 1, 0);
        } else {
            resultsTable.setValueAt(tokensPolarityProperty, 1, 0);
        }
        
        if (synsetsPolarityProperty instanceof Double) {
            resultsTable.setValueAt("Synset Polarity", 2, 0);
        } else {
            resultsTable.setValueAt(synsetsPolarityProperty, 2, 0);
        }

        resultsTable.setValueAt(tokensPolarityProperty, 1, 1);
        resultsTable.setValueAt(synsetsPolarityProperty, 2, 1);
	}
	
	/**
	 * Create 
	 * @param instances
	 * @param paginationPanel
	 */
	private void createPagination(Collection<Instance> instances, JPanel paginationPanel) {
		int numberOfInstances = instances.size();
		
		JButton previousInstanceButton = new JButton("◄");
		previousInstanceButton.setPreferredSize(new Dimension(50, 23));
		previousInstanceButton.setMinimumSize(new Dimension(50, 23));
		previousInstanceButton.setMaximumSize(new Dimension(50, 23));
        previousInstanceButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        paginationPanel.add(previousInstanceButton);
        
        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(1);
        formatter.setMaximum(numberOfInstances);
        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);
        JFormattedTextField currentInstance = new JFormattedTextField(formatter);
        currentInstance.setFont(new Font("Tahoma", Font.PLAIN, 16));
        currentInstance.setColumns(3);
        paginationPanel.add(currentInstance);
        
        currentInstance.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				showInstanceValues((Instance) instances.toArray()[Integer.parseInt(evt.getNewValue().toString())-1]);
			}
		});
        currentInstance.setText("1");
                
        JLabel separatorLabel = new JLabel("/");
        separatorLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        paginationPanel.add(separatorLabel);
        
        JLabel maxInstanceNumberLabel = new JLabel(String.valueOf(numberOfInstances));
        maxInstanceNumberLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        paginationPanel.add(maxInstanceNumberLabel);
        
        JButton nextInstanceButton = new JButton("►");
        nextInstanceButton.setPreferredSize(new Dimension(50, 23));
        nextInstanceButton.setMinimumSize(new Dimension(50, 23));
        nextInstanceButton.setMaximumSize(new Dimension(50, 23));
        nextInstanceButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        paginationPanel.add(nextInstanceButton);

        previousInstanceButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		int currentValue = Integer.parseInt(currentInstance.getText());
        		currentInstance.setText(String.valueOf(--currentValue));
        	}
        });
        
        nextInstanceButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    			int currentValue = Integer.parseInt(currentInstance.getText());
        		currentInstance.setText(String.valueOf(++currentValue));
        	}
        });
	}

}
