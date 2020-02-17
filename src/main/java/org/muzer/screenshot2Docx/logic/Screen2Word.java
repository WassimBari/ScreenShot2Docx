package org.muzer.screenshot2Docx.logic;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;


public class Screen2Word 
{
	private WordprocessingMLPackage wordMLPackage ;
	private File file;

	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	
	public void createPackage() throws Exception
	{			   
		if(file.exists())
			this.wordMLPackage=WordprocessingMLPackage.load(file);
		else
			this.wordMLPackage = WordprocessingMLPackage.createPackage();
	}
	
	public void writeImage(BufferedImage screenshot) throws Exception
	{
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	     ImageIO.write(screenshot, "jpg",baos);
	     byte data[]=baos.toByteArray();
	     addImage(wordMLPackage,data);
		 baos.reset();
		 baos.close();
	}
	public BufferedImage produceEditableImage(Rectangle rectangle) throws AWTException
	{
        Robot robot = new Robot();
        robot.delay(500);   
        if(rectangle == null)
        	rectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        
        return robot.createScreenCapture(rectangle);
	}
	public void partScreenShot(Rectangle rectangle) throws Exception
	{
        Robot robot = new Robot();
        robot.delay(500);        
        BufferedImage screenShot = robot.createScreenCapture(rectangle);
        writeImage(screenShot);
	}
    public void fullScreenShot() throws Exception
    {    
        Robot robot = new Robot();
		robot.delay(500);
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	    writeImage(screenShot);
    }
	public void autoScrollScreenShot() throws Exception
	{
		Robot robot = new Robot();
		robot.keyPress(17);
		robot.keyPress(36);
		robot.keyRelease(17);
		robot.keyRelease(36);

		robot.delay(500);
        BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(screenShot, "jpg",baos);
        byte data[]=baos.toByteArray();
        addImage( wordMLPackage,data);

    	BufferedImage screenShot2;
    	byte[] data2;
    	int count=0;
    	while(count<10)
    	{
    		robot.keyPress(34);
			robot.keyRelease(34);
			robot.delay(200);
			screenShot2 = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			baos=new ByteArrayOutputStream();
			ImageIO.write(screenShot2, "jpg", baos );
	        data2 = baos.toByteArray();
	        baos.reset();
	        baos.close();

			if(Arrays.equals(data,data2))
			{
				System.out.println("SAME IMAGE BREAK");
				break;
			}
			else
			{
				System.out.println("DIFFERENT IMAGE CONTINUE..");
			}
	        addImage(wordMLPackage,data2);
			screenShot =robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			baos=new ByteArrayOutputStream();
			ImageIO.write(screenShot, "jpg", baos );
	        data = baos.toByteArray();
	        baos.reset();
	        baos.close();
	        count++;
    	}
	}

    @SuppressWarnings("deprecation")
	public void addImage( WordprocessingMLPackage wordMLPackage,byte[] bytes) throws Exception
	{

			BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, bytes);
			Inline inline =imagePart.createImageInline("Filename hint","Alternative text",1,2,false);
	
			org.docx4j.wml.ObjectFactory factory = new org.docx4j.wml.ObjectFactory();
			org.docx4j.wml.P  para = factory.createP();
			org.docx4j.wml.R  run = factory.createR();             
			
			para.getParagraphContent().add(run);       
			org.docx4j.wml.Drawing drawing = factory.createDrawing(); 
			run.getRunContent().add(drawing);     
			drawing.getAnchorOrInline().add(inline);
			
			wordMLPackage.getMainDocumentPart().addObject(para);
			wordMLPackage.save(file);
			
	}
    public void addText(String text) throws Exception 
    {
		wordMLPackage.getMainDocumentPart().addParagraphOfText(text);
		wordMLPackage.save(file);
	}
}
