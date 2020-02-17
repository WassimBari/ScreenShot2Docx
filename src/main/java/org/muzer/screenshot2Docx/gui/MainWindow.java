package org.muzer.screenshot2Docx.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.muzer.screenshot2Docx.logic.Screen2Word;

import org.docx4j.openpackaging.exceptions.Docx4JException;



public class MainWindow implements Runnable,ActionListener
{
	Screen2Word screen2Word;
	JFrame window;
	JPanel toolsPanel,southPanel;
	JButton screenshot,partScreen,autoScroll,text,edit;
	JLabel status,signature;
	LineBorder lineBorder = new LineBorder(Color.GREEN,3);
	Border defaultBorder;
	boolean isEdit;
	static GridBagConstraints gc=new GridBagConstraints();

	public static void resetConstraints()
	{
		gc=new GridBagConstraints();
		gc.insets=new Insets(5,5,5,5);
		gc.ipadx=gc.ipady=2;
		gc.weightx=gc.weighty=1.0;
		gc.anchor=GridBagConstraints.LINE_START;
		gc.fill=GridBagConstraints.NONE;
	}
	public static GridBagConstraints setConstraints(int row,int col)
	{
		gc.gridx=row;
		gc.gridy=col;
		return gc;
	}
	public static GridBagConstraints setConstraints(int row,int col,int colspan)
	{
		gc.gridx=row;
		gc.gridy=col;
		gc.gridwidth=colspan;
		return gc;
	}
	public void repack()
	{
		window.setMinimumSize(window.getPreferredSize());
		window.setSize(window.getMinimumSize());
	}
	public void setError(String message)
	{
		if(message.isEmpty())
		{
			status.setForeground(Color.LIGHT_GRAY);
			status.setText(signature.getText());
		}
		else
		{
			status.setForeground(Color.RED);
			status.setText(message);
		}

	}
	
	public MainWindow(Screen2Word screen2Word,JLabel signature) {
		this.screen2Word = screen2Word;
		this.signature = signature;
		status = new JLabel();
	}
	public MainWindow()
	{
		signature = new JLabel("INSIDE");
		status = new JLabel();
	}
	
	void initialiseWindows()
	{
		window=new JFrame("ScreenShot2Docx");	
			window.setLayout(new BorderLayout());
			window.addWindowListener(new WindowListener() {
				
				@Override
				public void windowOpened(WindowEvent arg0) 
				{					
				}
				
				@Override
				public void windowIconified(WindowEvent arg0) {
				}
				
				@Override
				public void windowDeiconified(WindowEvent arg0) {
				}
				
				@Override
				public void windowDeactivated(WindowEvent arg0) {
				}
				
				@Override
				public void windowClosing(WindowEvent arg0) {
						Thread t=new Thread(new FileChooser());
						t.start();
						try 
						{
							StringBuilder cmd=new StringBuilder("cmd.exe /c start ");
							String fileparts[]=screen2Word.getFile().getAbsolutePath().split("[\\\\]");
							for(String filepart:fileparts)
							{
								if(filepart.trim().contains(" "))
									cmd.append("\""+filepart+"\""+"\\");
								else
									cmd.append(filepart+"\\");
							}
							cmd.deleteCharAt(cmd.lastIndexOf("\\"));
							System.out.println(cmd.toString());
							Runtime.getRuntime().exec(cmd.toString());
						} 
						catch (IOException e1) 
						{
							e1.printStackTrace();
						}
						window.dispose();
				}
				
				@Override
				public void windowClosed(WindowEvent arg0) {
				}
				
				@Override
				public void windowActivated(WindowEvent arg0) {
				}
			});
			window.setVisible(true);
			window.setResizable(true);
		resetConstraints();
	}

	private ImageIcon getImageIcon(final String imgName) {
		final String imageFile = String.format("/images/%s", imgName);
		return new ImageIcon(MainWindow.class.getResource(imageFile));
	}

	void designToolsPanel()
	{
		toolsPanel = new JPanel();
			toolsPanel.setLayout(new FlowLayout(FlowLayout.LEADING,4,4));
		screenshot =new JButton();
			screenshot.setIcon(getImageIcon("screenshot.png"));
			screenshot.setPreferredSize(new Dimension(35,35));
			screenshot.addActionListener(this);
			toolsPanel.add(screenshot);
		partScreen =new JButton();
			partScreen.setIcon(getImageIcon("partScreen.png"));
			partScreen.setPreferredSize(new Dimension(35,35));
			partScreen.addActionListener(this);
			toolsPanel.add(partScreen);
			
		autoScroll =new JButton();
			autoScroll.setIcon(getImageIcon("autoScroll.png"));
			autoScroll.setPreferredSize(new Dimension(35,35));
			autoScroll.addActionListener(this);
			toolsPanel.add(autoScroll);
		text =new JButton();			
			text.setIcon(getImageIcon("text.png"));
			text.setPreferredSize(new Dimension(35,35));
			text.addActionListener(this);
			toolsPanel.add(text);
		edit =new JButton();			
			edit.setIcon(getImageIcon("edit.png"));
			edit.addActionListener(this);
			edit.setPreferredSize(new Dimension(35,35));
			toolsPanel.add(edit);
		
		toolsPanel.setOpaque(false);
	}

	@Override
	public void run() 
	{
		JFrame.setDefaultLookAndFeelDecorated(true);
		initialiseWindows();
			window.add(new JLabel(screen2Word.getFile().getName()),BorderLayout.NORTH);
		designToolsPanel();
			window.add(toolsPanel);
			setError("");
			window.add(status,BorderLayout.SOUTH);
		window.setSize(new Dimension(600,600));
		window.setMinimumSize(window.getPreferredSize());
		window.pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) (screen.getWidth() - window.getWidth()-100);
		int y = (int) (screen.getHeight() - window.getHeight() -100);
		window.setLocation(x, y);
	}
	@Override
	public void actionPerformed(ActionEvent event) 
	{
		Object ob = event.getSource();
		if(ob==edit)
		{
			if(! isEdit)
			{
				edit.setBorder(lineBorder);
				screenshot.setBorder(lineBorder);
				partScreen.setBorder(lineBorder);
				isEdit=true;
			}
			else
			{
				edit.setBorder(text.getBorder());
				screenshot.setBorder(text.getBorder());
				partScreen.setBorder(text.getBorder());
			
				isEdit=false;
			}
		}
		if(ob==screenshot)
		{
			try 
			{
				setError("");
				window.setVisible(false);
				if(isEdit)
				{
					BufferedImage image = screen2Word.produceEditableImage(null);
			        ImageEditor imageEditor=new ImageEditor(image,this);
				    Thread t=new Thread(imageEditor);
				    t.start();
				}
				else
				{
					screen2Word.fullScreenShot();
					window.setVisible(true);
				}
	
			} 
			catch (Docx4JException fileOpened) 
			{
				System.out.println(fileOpened.fillInStackTrace());
				setError("File Already Open");
			}
			catch (Exception e1) 
			{
				setError(e1.getMessage());
			}
			finally
			{
				repack();
			}
		}
		if(ob==partScreen)
		{
			try 
			{
				setError("");
				window.setVisible(false);
				Rectangle rectangle=new Rectangle(window.getX(),window.getY(),window.getWidth(),window.getHeight());
				if(isEdit)
				{
					BufferedImage image = screen2Word.produceEditableImage(rectangle);
			        ImageEditor imageEditor=new ImageEditor(image,this);
				    Thread t=new Thread(imageEditor);
				    t.start();
				}
				else
				{
					screen2Word.partScreenShot(rectangle);
					window.setVisible(true);
				}
				
			} 
			catch (Docx4JException fileOpened) 
			{
				setError("File Already Open");
			}
			catch (Exception e1) 
			{
				setError(e1.getMessage());
				repack();
			}
			finally
			{
				repack();
			}
		}
		if(ob==autoScroll)
		{
			try 
			{
				setError("");
				window.setVisible(false);
				screen2Word.autoScrollScreenShot();
				window.setVisible(true);
			} 
			catch (Docx4JException fileOpened) 
			{
				setError("File Already Open");
			}
			catch (Exception e1) 
			{
				setError(e1.getMessage());
				repack();
			}
			finally
			{
				repack();
			}
		}
	   if(ob==text)
	   {
		   setError("");
		   final JOptionPane optionPane= new JOptionPane();
		   
		   final JTextArea textArea = new JTextArea(10,5);
		   textArea.setText("");
		   textArea.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent arg0) {
					optionPane.setInputValue(textArea.getText());
				}
				
				@Override
				public void keyReleased(KeyEvent arg0) {
					optionPane.setInputValue(textArea.getText());
				}
				
				@Override
				public void keyPressed(KeyEvent arg0) {
					optionPane.setInputValue(textArea.getText());		
				}
			});
		   
		   JScrollPane scrollPane=new JScrollPane(textArea,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		   scrollPane.setViewportView(textArea);
		   
		   optionPane.setMessage(new Object[] {"Enter Text",scrollPane});
		   optionPane.setMessageType(JOptionPane.DEFAULT_OPTION);
		   optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
		   JDialog dialog = optionPane.createDialog(window, "Write Text to Word");
		   dialog.setVisible(true);
		   if(optionPane.getValue() != null)
		   {
			   int result = (Integer) optionPane.getValue();
			   if( result==0)
			   {
				   System.out.println(optionPane.getInputValue());
				    try 
				    {
				       String text = (String)optionPane.getInputValue();
					   screen2Word.addText(text.trim());
				    }
					catch (Docx4JException fileOpened) 
					{
						setError("File Already Open");
					}
					catch (Exception e1) 
					{
						setError(""+e1.getLocalizedMessage());
						repack();
					}
					finally
					{
						repack();
					}
				  
			   }
		   }
	   }
	}
	
}
