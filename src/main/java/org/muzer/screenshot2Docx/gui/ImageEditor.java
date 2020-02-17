package org.muzer.screenshot2Docx.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.muzer.screenshot2Docx.logic.ImageEditorPanel;

import org.docx4j.openpackaging.exceptions.Docx4JException;

public class ImageEditor implements Runnable,ActionListener,KeyListener
{
	private JFrame window;
	private JPanel toolsPanel,signaturePanel;
	private JScrollPane scrollPane;
	private JButton done,colorPicker,clearDrawing;
	private ImageEditorPanel imageEditorPanel;
	private Color color=Color.RED;
	private JComboBox<Integer> strokeSize;
	private BufferedImage image;
	private final Integer[] strokeSizeList = {2,3,4,5};
	private boolean isJobDone;
	private MainWindow  newScreen;

	public ImageEditor(BufferedImage image,MainWindow mainWindow) {
		super();
		this.image = image;
		this.newScreen = mainWindow;
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public  boolean isJobDone() {
		return isJobDone;
	}
	public void setJobDone(boolean isJobDone) {
		this.isJobDone = isJobDone;
	}

	private void designToolsPanel() 
	{
		toolsPanel = new JPanel();
			toolsPanel.setLayout(new FlowLayout(5));
		colorPicker = new JButton();
			colorPicker.setPreferredSize(new Dimension(25,25));
			colorPicker.setBackground(color);
			colorPicker.addActionListener(this);
			toolsPanel.add(colorPicker);
			
		strokeSize = new JComboBox<Integer>(strokeSizeList);
			strokeSize.setEditable(false);
			strokeSize.setSelectedIndex(0);
			strokeSize.addItemListener(new ItemListener() {
				
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					if(strokeSize.getSelectedItem() != null)
						imageEditorPanel.setStrokeSize(strokeSize.getSelectedItem().hashCode());
					window.requestFocus();
				}
			});
			toolsPanel.add(strokeSize);
			
		clearDrawing = new JButton("clear");
			clearDrawing.addActionListener(this);
			toolsPanel.add(clearDrawing);
			
		done = new JButton("Done");
			done.addActionListener(this);
			toolsPanel.add(done);
		
	}

	private void designWindow()
	{
		window=new JFrame("Edit Screen Shot");
			window.setVisible(true);
			window.setResizable(true);
			window.setLayout(new BorderLayout(5,5));	
			window.addWindowListener(new WindowListener() {
				
				@Override
				public void windowOpened(WindowEvent arg0) {}
				
				@Override
				public void windowIconified(WindowEvent arg0) {}
				
				@Override
				public void windowDeiconified(WindowEvent arg0) {}
				
				@Override
				public void windowDeactivated(WindowEvent arg0) {}
				
				@Override
				public void windowClosing(WindowEvent arg0) {
					newScreen.window.setVisible(true);
					newScreen.repack();
					System.out.println("SET VISIBLE....");
				}
				
				@Override
				public void windowClosed(WindowEvent arg0) {}
				
				@Override
				public void windowActivated(WindowEvent arg0) {}
			});
	}
	
	public void createGUI()
	{
		designWindow();
		designToolsPanel();
		window.add(toolsPanel,BorderLayout.NORTH);
		window.setFocusable(true);
		window.setFocusTraversalPolicy(new FocusTraversalPolicy(){

			@Override
			public Component getComponentAfter(Container arg0, Component arg1) {
				// TODO Auto-generated method stub
				return window;
			}

			@Override
			public Component getComponentBefore(Container arg0, Component arg1) {
				// TODO Auto-generated method stub
				return window;
			}

			@Override
			public Component getDefaultComponent(Container arg0) {
				// TODO Auto-generated method stub
				return window;
			}

			@Override
			public Component getFirstComponent(Container arg0) {
				// TODO Auto-generated method stub
				return window;
			}

			@Override
			public Component getLastComponent(Container arg0) {
				// TODO Auto-generated method stub
				return window;
			}
			
		});
		window.addKeyListener(this);
		
		imageEditorPanel = new ImageEditorPanel(image);
			imageEditorPanel.setDrawingColor(color);
		
		scrollPane = new JScrollPane(imageEditorPanel);
			scrollPane.setAutoscrolls(true);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setPreferredSize(new Dimension(400,400));
			scrollPane.setViewportView(imageEditorPanel);
		window.add(scrollPane,BorderLayout.CENTER);
		
		signaturePanel = new JPanel();
			signaturePanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
			signaturePanel.add(newScreen.signature);
		window.add(signaturePanel,BorderLayout.PAGE_END);
		window.pack();
		window.setSize(window.getPreferredSize());
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((screen.getWidth() - window.getWidth()) /2);
		int y = (int) ((screen.getHeight() -window.getHeight()) /2);
		window.setLocation(x, y);
	}
	@Override
	public void run() 
	{
		createGUI();
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		
		Object source=event.getSource();
		
		if(source == colorPicker)
		{
			color = JColorChooser.showDialog(window,"choose Drawing Color",color);
			colorPicker.setBackground(color);
			imageEditorPanel.setDrawingColor(color);
		}
		
		if(source == clearDrawing)
		{
			imageEditorPanel.clearEditing();
		}
		if(source == done)
		{
			imageEditorPanel.updateEditing();
			image = imageEditorPanel.getImage();
			isJobDone=true;
			try 
			{
				finishJob();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			window.dispose();
		}
		window.requestFocus();
	}
	
	public void finishJob() 
	{
		try
		{
			newScreen.screen2Word.writeImage(image);
		}
		catch (Docx4JException fileOpened) 
		{
			newScreen.setError("File Already Open");
		}
		catch (Exception e1) 
		{
			newScreen.setError(e1.getMessage());
		}
		finally
		{
			newScreen.window.setVisible(true);
			newScreen.repack();
		}
	}

	@Override
	public void keyPressed(KeyEvent keyEvent) {

		int keyCode = keyEvent.getKeyCode();
                 
		if(keyEvent.isControlDown() && keyCode == 90)
			imageEditorPanel.undoEditing();
		if(keyEvent.isControlDown() && keyCode == 89)
			imageEditorPanel.redoEditing();
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
