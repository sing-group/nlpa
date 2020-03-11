package org.nlpa.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
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

public class MainUI {

	private JFrame frame;
	private JTable resultsTable;

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
	
    public static Object computePolarity(StringBuffer str) {
        Instance inst = new Instance(str, "polarity", "Test instance ID", str);
        inst.setProperty("language", "EN");

        AbstractPipe p = new SerialPipes(new AbstractPipe[]{new TargetAssigningFromPathPipe(),
        		new StoreFileExtensionPipe(), 
                new GuessDateFromFilePipe(), 
                new File2StringBufferPipe(),
                new MeasureLengthFromStringBufferPipe(),
                new FindUrlInStringBufferPipe(),
                new StripHTMLFromStringBufferPipe(),
                new MeasureLengthFromStringBufferPipe("length_after_html_drop"), 
                new GuessLanguageFromStringBufferPipe(),
                new ContractionsFromStringBufferPipe(),
                new AbbreviationFromStringBufferPipe(),
                new SlangFromStringBufferPipe(),
                new ComputePolarityFromLexiconPipe()
        });

        if (!p.checkDependencies()) {
            System.out.println("Pipe dependencies are not satisfied");
          System.out.println(AbstractPipe.getErrorMessage());
            System.exit(1);
        } else {
            System.out.println("Pipe dependencies are satisfied");
        }

        Instance resultInstance = p.pipe(inst);

        return resultInstance.getProperty("polarity");
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
        
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(1100, 500));
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;
        frame.getContentPane().add(panel, gbc_panel);
        panel.setLayout(new GridLayout(1, 0, 0, 0));
        panel.setBorder(new EmptyBorder(20, 20, 0, 20));
        
        JPanel panel_1 = new JPanel();
        panel_1.setBackground(Color.WHITE);
        panel.add(panel_1);
        panel_1.setBorder(new EmptyBorder(20, 20, 20, 10));
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[]{497, 0};
        gbl_panel_1.rowHeights = new int[] {50, 290, 50, 0};
        gbl_panel_1.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel_1.setLayout(gbl_panel_1);
        
        JPanel panel_3 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel_3.setPreferredSize(new Dimension(500, 100));
        panel_3.setMaximumSize(new Dimension(500, 100));
        panel_3.setBackground(Color.WHITE);
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.fill = GridBagConstraints.BOTH;
        gbc_panel_3.insets = new Insets(0, 0, 5, 0);
        gbc_panel_3.gridx = 0;
        gbc_panel_3.gridy = 0;
        panel_1.add(panel_3, gbc_panel_3);
        
        JLabel messageLabel = new JLabel("Test with your own text:");
        messageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        messageLabel.setBackground(Color.WHITE);
        messageLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel_3.add(messageLabel);
        
        JPanel panel_4 = new JPanel();
        panel_4.setBackground(Color.WHITE);
        GridBagConstraints gbc_panel_4 = new GridBagConstraints();
        gbc_panel_4.fill = GridBagConstraints.BOTH;
        gbc_panel_4.insets = new Insets(0, 0, 5, 0);
        gbc_panel_4.gridx = 0;
        gbc_panel_4.gridy = 1;
        panel_1.add(panel_4, gbc_panel_4);
        panel_4.setLayout(new GridLayout(0, 1, 0, 0));
        
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputTextArea.setLineWrap(true);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(inputTextArea);
        panel_4.add(scrollPane);
        
        JPanel panel_5 = new JPanel();
        panel_5.setBackground(Color.WHITE);
        GridBagConstraints gbc_panel_5 = new GridBagConstraints();
        gbc_panel_5.fill = GridBagConstraints.BOTH;
        gbc_panel_5.gridx = 0;
        gbc_panel_5.gridy = 2;
        panel_1.add(panel_5, gbc_panel_5);
        panel_5.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton classifyButton = new JButton("Classify");
        classifyButton.setPreferredSize(new Dimension(100, 23));
        classifyButton.setMinimumSize(new Dimension(100, 23));
        classifyButton.setMaximumSize(new Dimension(100, 23));
        classifyButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        classifyButton.setActionCommand("clasify");
        
        panel_5.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel_5.add(classifyButton);
        
        JButton clearButton = new JButton("Clear");
        clearButton.setPreferredSize(new Dimension(100, 23));
        clearButton.setMinimumSize(new Dimension(100, 23));
        clearButton.setMaximumSize(new Dimension(100, 23));
        clearButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        	}
        });
        clearButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel_5.add(clearButton);
        
        JPanel panel_2 = new JPanel();
        panel_2.setBackground(Color.WHITE);
        panel.add(panel_2);
        panel_2.setBorder(new EmptyBorder(20, 10, 20, 20));
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{497, 0};
        gbl_panel_2.rowHeights = new int[] {50, 340, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);
        
        JPanel panel_6 = new JPanel();
        panel_6.setBackground(Color.WHITE);
        GridBagConstraints gbc_panel_6 = new GridBagConstraints();
        gbc_panel_6.fill = GridBagConstraints.BOTH;
        gbc_panel_6.insets = new Insets(0, 0, 5, 0);
        gbc_panel_6.gridx = 0;
        gbc_panel_6.gridy = 0;
        panel_2.add(panel_6, gbc_panel_6);
        panel_6.setLayout(new GridLayout(0, 2, 0, 0));
        
        JPanel panel_8 = new JPanel();
        panel_8.setBackground(Color.WHITE);
        panel_6.add(panel_8);
        panel_8.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        JLabel resultsLabel = new JLabel("Results:");
        resultsLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        resultsLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        panel_8.add(resultsLabel);
        
        JPanel panel_9 = new JPanel();
        panel_9.setBackground(Color.WHITE);
        panel_6.add(panel_9);
        panel_9.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JPanel panel_7 = new JPanel();
        panel_7.setBackground(Color.WHITE);
        GridBagConstraints gbc_panel_7 = new GridBagConstraints();
        gbc_panel_7.fill = GridBagConstraints.BOTH;
        gbc_panel_7.gridx = 0;
        gbc_panel_7.gridy = 1;
        panel_2.add(panel_7, gbc_panel_7);
        panel_7.setLayout(new GridLayout(0, 1, 0, 0));
        
        resultsTable = new JTable();
        resultsTable.setVisible(false);
        resultsTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        resultsTable.setShowVerticalLines(false);
        resultsTable.setRowSelectionAllowed(false);
        resultsTable.setRowHeight(50);
        resultsTable.setRowMargin(1);
        panel_7.add(resultsTable);
        
        // Create table model so the table is displayed
        resultsTable.setModel(new DefaultTableModel(2, 2));
        
        JPanel panel_10 = new JPanel();
        panel_10.setBorder(new EmptyBorder(0, 20, 5, 20));
        GridBagConstraints gbc_panel_10 = new GridBagConstraints();
        gbc_panel_10.fill = GridBagConstraints.BOTH;
        gbc_panel_10.gridx = 0;
        gbc_panel_10.gridy = 1;
        frame.getContentPane().add(panel_10, gbc_panel_10);
        panel_10.setLayout(new GridLayout(0, 1, 0, 0));
        
        JTextArea notificationTextArea = new JTextArea();
        notificationTextArea.setBorder(new EmptyBorder(5, 0, 0, 0));
        notificationTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        notificationTextArea.setDisabledTextColor(SystemColor.desktop);
        notificationTextArea.setBackground(SystemColor.control);
        notificationTextArea.setAutoscrolls(false);
        notificationTextArea.setEnabled(false);
        notificationTextArea.setEditable(false);
        panel_10.add(notificationTextArea);
        
        // Set table headers
        resultsTable.setValueAt("<html><b>PROPERTY</b></html>", 0, 0);
        resultsTable.setValueAt("<html><b>VALUE</b></html>", 0, 1);
        
        // Set right column alignment
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        resultsTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        classifyButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		StringBuffer text = new StringBuffer(inputTextArea.getText());
        		
                Object polarity = computePolarity(text);

                if (polarity instanceof Double) {
                    resultsTable.setValueAt("Polarity", 1, 0);
                } else {
                    resultsTable.setValueAt(polarity, 1, 0);
                }

                resultsTable.setValueAt(polarity, 1, 1);
                
                resultsTable.setVisible(true);
                notificationTextArea.setText("The text has been computed");
        	}
        });
        
        clearButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		resultsTable.setVisible(false);
        		inputTextArea.setText("");
        		notificationTextArea.setText("");
        	}
        });
	}

}
