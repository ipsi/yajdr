package yajdr.core;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yajdr.gui.LoadingScreen;

/**
 * This is the class that contains the main method for this application. Also, as of 9.42pm,
 * 27/05/2005, this project contains 4,423 lines of source code, including comments
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 */

public class DieRollerMain
{

	private static final Log	log	= LogFactory.getLog(DieRollerMain.class);



	/**
	 * This creates a new instance of <code>DiceRoller</code>, sets its dimensions, makes it
	 * visible, and sets it so that it will exit on close by default.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch (Exception e)
		{
			log.debug("Caught an exception trying to load the L&F...", e);
		}

		LoadingScreen ls = new LoadingScreen();
		final DieRoller dr = new DieRoller(ls);
		dr.pack();
		dr.setVisible(true);
		dr.setTitle("Storyteller System Dice Roller");
		dr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ls.setVisible(false);
		// log.trace("Loading display...");
		// Display disp = Display.getDefault();
		// Shell shell = new Shell(disp, SWT.SHELL_TRIM);
		//
		// GridLayout layout = new GridLayout();
		// layout.numColumns = 2;
		// layout.makeColumnsEqualWidth = true;
		// shell.setLayout(layout);
		//
		// Label label = new Label(shell, SWT.HORIZONTAL | SWT.SHADOW_NONE | SWT.LEAD);
		// label.setText("This is a label.");
		//
		// GridData data = new GridData(GridData.FILL_BOTH);
		// label.setLayoutData(data);
		//
		// shell.pack();
		// shell.open();
		//
		// while (!shell.isDisposed())
		// {
		// if (!disp.readAndDispatch())
		// disp.sleep();
		// }
		// disp.dispose();
	}



	public static Point centreOnScreen(Dimension d)
	{
		Dimension c = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((c.width - d.width) / 2, (c.height - d.height) / 2);
	}
}
