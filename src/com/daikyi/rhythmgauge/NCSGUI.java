package com.daikyi.rhythmgauge;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.embed.swing.JFXPanel;

public class NCSGUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel fileTitle = new JLabel();
	private JFXPanel fxPanel = new JFXPanel();
	private static final JButton loadFile = new JButton("Load File");
	
	public NCSGUI(){
		initAndShowGUI();
	}
	
    private void initAndShowGUI() {
    	
        // This method is invoked on the EDT thread
        JFrame frame = new JFrame("NCS Utility");
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.LINE_AXIS));
        
        infoPanel.add(loadFile);
        infoPanel.add(fileTitle);
        
        panel.add(infoPanel);
        panel.add(fxPanel);
        fxPanel.setVisible(false);
        loadFile.addActionListener(this);
        
        frame.add(panel);
        frame.setSize(500, 200);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void initFX(String filePath) {
    	
		FileParser file = new SMFileParser(filePath);
		file.parseFile();
        fxPanel.setScene(GraphUtility.getGraph(file));
        fxPanel.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new NCSGUI();
            }
        });
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		
    	//Using JFileChooser, allow user to pick file path
        JFileChooser chartFile = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Stepmania Charts", "sm");
        chartFile.setFileFilter(filter);
        int returnVal = chartFile.showOpenDialog(this);
        
        //when the user selects a file, read the file and load the file
        if(returnVal == JFileChooser.APPROVE_OPTION) {
        	fxPanel.setVisible(false);
        	fileTitle.setText(chartFile.getSelectedFile().getName());
    		initFX(chartFile.getSelectedFile().getPath());
        }//end if	
	}
}
