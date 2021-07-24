package org.nlpa.iu;

import org.bdp4j.types.Instance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

public class MainUI {
    private JFrame frame;
    private File selectedFolder = null;

    public static void initUI (){
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

    public MainUI() {
        initialize();
    }

    private void initialize() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame();
        frame.setResizable(false);
        frame.setMaximumSize(new Dimension(1280, 600));
        frame.setMinimumSize(new Dimension(1280, 600));
        frame.setPreferredSize(new Dimension(1280, 600));
        frame.getContentPane().setPreferredSize(new Dimension(1280, 600));
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{1280, 0};
        gridBagLayout.rowHeights = new int[] {520, 30, 0};
        frame.getContentPane().setLayout(gridBagLayout);
        gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};

        //Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setSize(new Dimension(1280, 600));
        mainPanel.setBorder(new LineBorder(new Color(255, 255, 255)));
        frame.getContentPane().add(mainPanel);
        mainPanel.setLayout(new GridLayout(1, 0, 0, 0));
        mainPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        //Panel de la izquierda
        JPanel leftPanel = new JPanel();
        mainPanel.add(leftPanel);
        GridBagLayout gblLeftPanel = new GridBagLayout();
        gblLeftPanel.columnWidths = new int[] { 550, 50 };
        gblLeftPanel.rowHeights = new int[] { 10, 400, 10 };
        gblLeftPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gblLeftPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        leftPanel.setLayout(gblLeftPanel);

        //Panel Cabecera Izquierda
        JPanel leftHeaderPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) leftHeaderPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        leftHeaderPanel.setPreferredSize(new Dimension(600, 200));
        leftHeaderPanel.setMaximumSize(new Dimension(500, 100));
        leftHeaderPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_leftHeaderPanel = new GridBagConstraints();
        gbc_leftHeaderPanel.fill = GridBagConstraints.BOTH;
        gbc_leftHeaderPanel.insets = new Insets(0, 0, 5, 0);
        gbc_leftHeaderPanel.gridx = 0;
        gbc_leftHeaderPanel.gridy = 0;
        leftPanel.add(leftHeaderPanel, gbc_leftHeaderPanel);

        //Mensaje de cabecera izquierda
        JLabel messageLabel = new JLabel("Type text here or select a folder: ");
        messageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        messageLabel.setBackground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        leftHeaderPanel.add(messageLabel);

        //Panel de texto izquierdo
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_textPanel = new GridBagConstraints();
        gbc_textPanel.fill = GridBagConstraints.BOTH;
        gbc_textPanel.insets = new Insets(0, 0, 5, 0);
        gbc_textPanel.gridx = 0;
        gbc_textPanel.gridy = 1;
        leftPanel.add(textAreaPanel, gbc_textPanel);
        textAreaPanel.setLayout(new GridLayout(0, 1, 0, 0));

        //Input text area
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setDisabledTextColor(Color.BLACK);
        inputTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputTextArea.setLineWrap(true);

        //Scroll del texto
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setViewportView(inputTextArea);
        textAreaPanel.add(scrollPane);

        //Panel de notificación
        JPanel notificationPanel = new JPanel();
        notificationPanel.setBackground(UIManager.getColor("TextArea.inactiveForeground"));
        GridBagConstraints gbcNotification = new GridBagConstraints();
        gbcNotification.fill = GridBagConstraints.BOTH;
        gbcNotification.gridx = 0;
        gbcNotification.gridy = 1;
        frame.getContentPane().add(notificationPanel, gbcNotification);
        notificationPanel.setLayout(new FlowLayout());

        //Area de notificacion
        JTextArea notificationTA = new JTextArea();
        notificationTA.setBackground(UIManager.getColor("TextArea.inactiveForeground"));
        notificationTA.setOpaque(false);
        notificationTA.setForeground(Color.BLACK);
        notificationTA.setEnabled(true);
        notificationTA.setEditable(false);
        notificationTA.setAutoscrolls(true);
        notificationTA.setFont(new Font("Monospaced", Font.BOLD, 14));
        notificationPanel.add(notificationTA);

        //Botones del panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
        gbc_buttonsPanel.fill = GridBagConstraints.BOTH;
        gbc_buttonsPanel.gridx = 0;
        gbc_buttonsPanel.gridy = 2;
        leftPanel.add(buttonsPanel, gbc_buttonsPanel);
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton startButton = new JButton("Process");
        startButton.setPreferredSize(new Dimension(105, 23));
        startButton.setMinimumSize(new Dimension(105, 23));
        startButton.setMaximumSize(new Dimension(105, 23));
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setActionCommand("classify");

        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton openFilesButton = new JButton("File");
        openFilesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                selectedFolder = showFileChooser();

                if (selectedFolder != null) {
                    inputTextArea.setEnabled(false);
                    inputTextArea.setBackground(Color.LIGHT_GRAY);

                    // Information about the selected folder
                    StringBuffer folderInfo = new StringBuffer();
                    folderInfo.append(selectedFolder.getPath());
                    folderInfo.append(" directory has been selected to process");

                    notificationTA.setText(folderInfo.toString());
                }
            }
        });
        openFilesButton.setPreferredSize(new Dimension(105, 23));
        openFilesButton.setMinimumSize(new Dimension(105, 23));
        openFilesButton.setMaximumSize(new Dimension(105, 23));
        openFilesButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonsPanel.add(openFilesButton);
        buttonsPanel.add(startButton);

        //Limpiar paneles
        JButton clearButton = new JButton("Clear Panel");
        clearButton.setPreferredSize(new Dimension(150, 23));
        clearButton.setMinimumSize(new Dimension(105, 23));
        clearButton.setMaximumSize(new Dimension(105, 23));
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonsPanel.add(clearButton);

        //Panel derecho de resultados
        JPanel rightPanel = new JPanel();
        mainPanel.add(rightPanel);
        GridBagLayout gblRightPanel = new GridBagLayout();
        gblRightPanel.columnWidths = new int[] { 550, 50 };
        gblRightPanel.rowHeights = new int[] { 10, 400, 10 };
        gblRightPanel.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gblRightPanel.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        rightPanel.setLayout(gblRightPanel);

        //Panel Cabecera izquierda
        JPanel rightHeaderPanel = new JPanel();
        FlowLayout anotherFlowLayout = (FlowLayout) rightHeaderPanel.getLayout();
        anotherFlowLayout.setAlignment(FlowLayout.LEFT);
        rightHeaderPanel.setPreferredSize(new Dimension(100, 200));
        rightHeaderPanel.setMaximumSize(new Dimension(100, 100));
        rightHeaderPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_rightHeaderPanel = new GridBagConstraints();
        gbc_rightHeaderPanel.fill = GridBagConstraints.BOTH;
        gbc_rightHeaderPanel.insets = new Insets(0, 0, 5, 0);
        gbc_rightHeaderPanel.gridx = 0;
        gbc_rightHeaderPanel.gridy = 0;
        rightPanel.add(rightHeaderPanel, gbc_rightHeaderPanel);

        //Mensaje de cabecera derecha
        JLabel rightMessageLabel = new JLabel("Result: ");
        rightMessageLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        rightMessageLabel.setBackground(Color.WHITE);
        rightMessageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        rightHeaderPanel.add(rightMessageLabel);

        //Panel de texto derecha
        JPanel textRightAreaPanel = new JPanel();
        textRightAreaPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_rightTextPanel = new GridBagConstraints();
        gbc_rightTextPanel.fill = GridBagConstraints.BOTH;
        gbc_rightTextPanel.insets = new Insets(0, 0, 5, 0);
        gbc_rightTextPanel.gridx = 0;
        gbc_rightTextPanel.gridy = 1;
        rightPanel.add(textRightAreaPanel, gbc_rightTextPanel);
        textRightAreaPanel.setLayout(new GridLayout(0, 1, 0, 0));

        //Input text area derecho
        JTextArea inputRightTextArea = new JTextArea();
        inputRightTextArea.setDisabledTextColor(Color.BLACK);
        inputRightTextArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        inputRightTextArea.setLineWrap(true);
        inputRightTextArea.setEnabled(false);
        
        //Scroll del texto derecho
        JScrollPane rightScrollPanel = new JScrollPane();
        rightScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rightScrollPanel.setViewportView(inputRightTextArea);
        textRightAreaPanel.add(rightScrollPanel);

        //Botones del panel derecho
        JPanel buttonsRightPanel = new JPanel();
        buttonsRightPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc_buttonsRightPanel = new GridBagConstraints();
        gbc_buttonsRightPanel.fill = GridBagConstraints.BOTH;
        gbc_buttonsRightPanel.gridx = 0;
        gbc_buttonsRightPanel.gridy = 2;
        rightPanel.add(buttonsRightPanel, gbc_buttonsRightPanel);
        buttonsRightPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        //Botones para guardar el texto del panel derecho en un archivo
        JButton outputButton = new JButton("Save text");
        outputButton.setPreferredSize(new Dimension(105, 23));
        outputButton.setMinimumSize(new Dimension(105, 23));
        outputButton.setMaximumSize(new Dimension(105, 23));
        outputButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonsRightPanel.add(outputButton);
        buttonsRightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        outputButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String text = inputRightTextArea.getText();
                if (!text.isEmpty()){
                    if (WriteToFile(text)){
                        notificationTA.setText("Text saved in output.txt");
                    }else{
                        notificationTA.setText("There was a problem saving the text");
                    }
                }else{
                    notificationTA.setText("There's no text to save");
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                Collection<Instance> instances = null;

                if (selectedFolder == null) {
                    if (inputTextArea.getText().isEmpty()) {
                        notificationTA.setText("Write a text or select a folder to process");
                    } else {
                        startButton.setEnabled(false);
                        StringBuffer text = new StringBuffer(inputTextArea.getText());
                        instances  = AppCore.findEntitiesInString(text);
                        inputRightTextArea.setText(showInstanceResults(instances));
                        notificationTA.setText("Results ready in the right panel.");
                    }
                }
                else {
                   startButton.setEnabled(false);
                   AppCore.generateInstances(selectedFolder.getAbsolutePath());
                   instances = AppCore.findEntitiesInFiles();
                   inputRightTextArea.setText(showInstanceResults(instances));
                   notificationTA.setText("Results ready in the right panel.");
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                inputTextArea.setText("");
                inputTextArea.setEnabled(true);
                inputTextArea.setBackground(Color.WHITE);
                inputRightTextArea.setText("");
                selectedFolder = null;
                notificationTA.setText("");
                startButton.setEnabled(true);
            }
        });

    }
    public String showInstanceResults(Collection<Instance> instances) {
        StringBuilder sb = new StringBuilder();
        for (Instance instance : instances){
            sb.append((String) instance.getProperty("FASTNERDATE"));
            sb.append((String) instance.getProperty("FASTNERCURRENCY"));
            sb.append((String) instance.getProperty("REGEXPNERDATE")) ;
            sb.append((String) instance.getProperty("REGEXPNERCURRENCY"));
        }
        return sb.toString();
    }

    public Boolean WriteToFile (String text){
        if (text.isEmpty()){
            return false;
        }
        try{
            FileWriter writer = new FileWriter("./output/output.txt");
            writer.write(text);
            writer.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }
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
}

