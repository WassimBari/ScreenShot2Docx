package org.muzer.screenshot2Docx.launcher;

import org.muzer.screenshot2Docx.gui.FileChooser;

public class ScreenShot2DocxLauncher
{
		public static void main(String s[]) throws Exception
		{
			Thread t=new Thread(new FileChooser());
			t.start();
		}
}
