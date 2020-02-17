package org.muzer.screenshot2Docx.logic;

import java.awt.Color;
import java.awt.Rectangle;

public class Drawing 
{
	Rectangle rectangle;
	String text;
	Color drawingColor;
	int strokeSize;
	
	public Drawing()
	{
		
	}
	public Drawing(Drawing copy)
	{
		if(copy.rectangle != null)
			this.rectangle = new Rectangle(copy.rectangle);
		this.text = copy.text;
		this.drawingColor = new Color(copy.getDrawingColor().getRGB());
		this.strokeSize = copy.strokeSize;
	}
	public Drawing(Rectangle rectangle,Color drawingColor,
			int strokeSize) {
		super();
		this.rectangle = rectangle;
		this.drawingColor = drawingColor;
		this.strokeSize = strokeSize;
	}
	
	
	public Drawing(Rectangle rectangle, String text, Color drawingColor,
			int strokeSize) {
		super();
		this.rectangle = rectangle;
		this.text = text;
		this.drawingColor = drawingColor;
		this.strokeSize = strokeSize;
	}
	
	public Rectangle getRectangle() {
		return rectangle;
	}
	public void setRectangle(Rectangle rectangle) {
		this.rectangle = rectangle;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Color getDrawingColor() {
		return drawingColor;
	}
	public void setDrawingColor(Color drawingColor) {
		this.drawingColor = drawingColor;
	}
	public int getStrokeSize() {
		return strokeSize;
	}
	public void setStrokeSize(int strokeSize) {
		this.strokeSize = strokeSize;
	}
}
