package org.muzer.screenshot2Docx.logic;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;


@SuppressWarnings("serial")
public class ImageEditorPanel extends JPanel implements MouseInputListener 
{
	private BufferedImage image;
	private BufferedImage originalImage;
	private Graphics2D graphics2D;
	private Point endPoint = null;
	private Point fixedPoint = null;
	private Rectangle bounds=null;
	private int strokeSize=2;
	private Color drawingColor=Color.black;
	private Drawing drawing,lastDrawing;
	private Stack<Drawing> drawingStack; 
	
	public ImageEditorPanel() {
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
		drawingStack = new Stack<Drawing>();
	}
	public ImageEditorPanel(BufferedImage image) {
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
		setImage(image);
		drawingStack = new Stack<Drawing>();
	}
   
	public BufferedImage getImage() {
        BufferedImage bi = new BufferedImage(image.getWidth(),image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        paint(g);
        return bi;
//		return image;
	}
	public void setImage(BufferedImage image) {
		
		this.originalImage = image;		
		createNewCopy();
	}
	private void createNewCopy()
	{
		ColorModel cm = originalImage.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = originalImage.copyData(null);
		this.image = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

		this.graphics2D = (Graphics2D) this.image.getGraphics();
		this.graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		this.graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
		this.graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		Font textFont = new Font("Arial", Font.BOLD, 16);
		this.graphics2D.setFont(textFont);

		Dimension size = new Dimension();
		size.height = image.getHeight();
		size.width = image.getWidth();
		
		bounds = new Rectangle();
		bounds.x=bounds.y=0;
		bounds.width = size.width;
		bounds.height = size.height;
				
		this.setPreferredSize(size);
	}
	
	public int getStrokeSize() {
		return strokeSize;
	}
	public void setStrokeSize(int strokeSize) {
		this.strokeSize = strokeSize;
	}
	
	public Color getDrawingColor() {
		return drawingColor;
	}
	public void setDrawingColor(Color drawingColor) {
		this.drawingColor = drawingColor;
	}
	
	public void clearEditing()
	{
		drawingStack.clear();
		createNewCopy();
		repaint();
	}
	
	public void undoEditing()
	{
		if(!drawingStack.isEmpty())
			lastDrawing = drawingStack.pop();
		createNewCopy();
		repaint();
	}
	
	public void redoEditing()
	{
		if(lastDrawing != null)
		{
			drawingStack.push(new Drawing(lastDrawing));
			lastDrawing = null;
		}
		createNewCopy();
		repaint();
	}
	
	public void updateEditing(){
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		this.originalImage = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	private void formRectangle()
	{		
		if(endPoint.x < 0)
			endPoint.x = 0;
		if(endPoint.y<0)
			endPoint.y=0;
		drawing.rectangle.width = Math.abs(fixedPoint.x - (int) Math.min(endPoint.x,bounds.getMaxX()-1) );
		drawing.rectangle.height = Math.abs(fixedPoint.y-(int) Math.min(endPoint.y,bounds.getMaxY()-1));
		drawing.rectangle.x = Math.min(fixedPoint.x,endPoint.x);
		drawing.rectangle.y = Math.min(fixedPoint.y,endPoint.y);
	}

	private void paintRectangle(Graphics2D graphics,boolean isRecOnly) {
		if(drawing.rectangle.height == 0 && drawing.rectangle.width ==0)
		{
			drawingStack.remove(drawing);
			return;
		}
		graphics.setColor(drawing.drawingColor);
		graphics.setStroke(new BasicStroke(drawing.strokeSize));
		graphics.draw(drawing.rectangle);

		if(isRecOnly)
			return;
		
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	
		
		String text = drawing.text;
		if(text != null && !text.isEmpty())
		{
			Font f = graphics.getFont();
			Font newFont = new Font(f.getFontName(),f.getStyle(),13);
			graphics.setFont(newFont);
			FontMetrics textMetrics = graphics.getFontMetrics();  
			int textWidth = textMetrics.stringWidth(text);
			int textHeight = textMetrics.getHeight();
			int centeredX = ((drawing.rectangle.width)/2) - (textWidth/2);

			int textStartX = drawing.rectangle.x+centeredX;
			int textStartY = (int)drawing.rectangle.getMinY()-3;
			
			if(textStartX <0)
				textStartX=0;
			if(textStartX+textWidth > image.getWidth())
				textStartX = image.getWidth()-textWidth-1;
		
			if(textStartY<0)
				textStartY = (int) (drawing.rectangle.getMaxY())+textHeight;
			
			Graphics2D temp = (Graphics2D) graphics;
			
			temp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			temp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			temp.setRenderingHint(RenderingHints.KEY_RENDERING,
	                RenderingHints.VALUE_RENDER_QUALITY);
			temp.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			temp.setFont(newFont);
			
			temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.7f));
			temp.setColor(Color.WHITE);
			temp.fill(new Rectangle(textStartX-1, 
					textStartY-textHeight+1,
					textWidth+2,
					textHeight+1));
			
			temp.setColor(drawing.drawingColor);
			temp.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,1f));

			temp.drawString(text,textStartX,textStartY);
			temp.dispose();
		}
		repaint();
	}
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		graphics.drawImage(image,0,0,null);
		
		for(int i=0;i<drawingStack.size();i++)
		{
			Drawing draw = drawingStack.get(i);
			if(draw != null && draw.rectangle != null)
			{
				drawing = new Drawing(draw);
				Graphics2D g2D = (Graphics2D) graphics.create();;
				paintRectangle(g2D,false);
				g2D.dispose();
			}
		}
		if(!drawingStack.isEmpty())
			drawing = drawingStack.peek();

	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub	
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}	
	
	@Override
	public void mousePressed(MouseEvent event) {
		if(bounds.contains(event.getPoint()))
		{
			endPoint = event.getPoint();
			fixedPoint = event.getPoint();
			Rectangle rectangle = new Rectangle();
			rectangle.setLocation(endPoint);
			
			drawing = new Drawing(rectangle, drawingColor, strokeSize);
			drawingStack.push(new Drawing(drawing));
			lastDrawing = null;
		}
	}
	@Override
	public void mouseDragged(MouseEvent event) {
		endPoint = event.getPoint();
		formRectangle();
		drawing.rectangle.width += 1;
		drawing.rectangle.height += 1;
		if(drawing != null)
			if(drawing.rectangle.height != 0 || drawing.rectangle.width != 0)
				repaint(drawing.rectangle.x,drawing.rectangle.y,drawing.rectangle.width,drawing.rectangle.height);

	
	}
	@Override
	public void mouseReleased(MouseEvent event) {
		
		if(drawing != null)
		{
			if (drawing.rectangle.width != 0 || drawing.rectangle.height != 0)
			{
				formRectangle();
				drawing.text = JOptionPane.showInputDialog(null);
				paintRectangle(graphics2D,false);
			}
			drawing = null;
		}
	}	
	
}
