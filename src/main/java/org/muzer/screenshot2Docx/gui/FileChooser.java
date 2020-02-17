package org.muzer.screenshot2Docx.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.muzer.screenshot2Docx.logic.Screen2Word;

public class FileChooser implements Runnable,ActionListener
{
	Screen2Word screen2Word;
	JFrame window;
	JPanel MainPanel,ButtonPanel;
	JButton outputFile,startProgram;
	JFileChooser fileChooser;
	FileFilter docfilter;
	JLabel info,fileInfo;
	java.io.File file;
    
	public FileChooser()
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		screen2Word = new Screen2Word();
		window=new JFrame("SELECT OUTPUT FILE");
		outputFile=new JButton("Select/Create file");
		startProgram=new JButton("start program");
		fileChooser=new JFileChooser();
		{
			docfilter = new FileNameExtensionFilter("DOC file", "doc", "docx");
			fileChooser.setFileFilter(docfilter);
			fileChooser.setAcceptAllFileFilterUsed(false);
		}
		
		info=new JLabel("created by : Wassim Bari ");
		info.setForeground(Color.LIGHT_GRAY);
		
		fileInfo=new JLabel("No File Selected");
		fileInfo.setForeground(Color.BLUE);
		
		ButtonPanel=new JPanel();
		MainPanel=new JPanel();
	}
	
	public void run() 
	{
		window.setMinimumSize(new Dimension(300,150));
		window.setPreferredSize(window.getMinimumSize());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screen.getWidth() - window.getWidth()) /2);
		int y = (int) ((screen.getHeight() -window.getHeight()) /2);
		window.setLocation(x, y); 
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.setResizable(true);
		window.setLayout(new FlowLayout());
		
		outputFile.addActionListener(this);
		startProgram.addActionListener(this);
		startProgram.setEnabled(false);
		MainPanel.setLayout(new BorderLayout(5,5));
		
		ButtonPanel.setLayout(new FlowLayout(5));
			ButtonPanel.add(outputFile);
			ButtonPanel.add(startProgram);	

		MainPanel.add(ButtonPanel,BorderLayout.NORTH);
		MainPanel.add(fileInfo,BorderLayout.CENTER);
		MainPanel.add(info,BorderLayout.AFTER_LAST_LINE);
		
		
		window.add(MainPanel);
		window.pack();
	}
	public void actionPerformed(ActionEvent e) 
	{
	    if (e.getSource() ==outputFile ) 
	    {
	        int returnVal = fileChooser.showOpenDialog(window);

	        if (returnVal == JFileChooser.APPROVE_OPTION) 
	        {
	            file = fileChooser.getSelectedFile();
	            if(docfilter.accept(file))
	            {
	            	fileInfo.setForeground(Color.BLUE);
	            	fileInfo.setText("Selected File: " + file.getName());
	            	startProgram.setEnabled(true);
	            }
	            else
	            {
	            	fileInfo.setForeground(Color.RED);
	            	fileInfo.setText("Not A Valid doc file..");
	            }
	        } 
	        else
	        {
	        	fileInfo.setForeground(Color.BLUE);
	        	fileInfo.setText("No File Selected");
	        }

	   } 
	   if(e.getSource()==startProgram)
	   {
		   fileInfo.setText("Opening File Please Wait ...");
		   screen2Word.setFile(file);
		   try
		   {
			   screen2Word.createPackage();
			   Thread tq=new Thread(new MainWindow(screen2Word,info));
			   tq.start();
			   window.dispose();
		   }
		   catch(Exception exception)
		   {
			   System.out.println(exception.getCause());
			   fileInfo.setForeground(Color.RED);
			   fileInfo.setText("File Template doesn't match , choose a different file");
			   file=null;
			   startProgram.setEnabled(false);
		   }
	   }
	}
}
