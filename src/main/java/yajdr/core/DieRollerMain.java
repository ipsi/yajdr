package yajdr.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import yajdr.gui.LoadingScreen;

import javax.swing.*;
import java.awt.*;

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
		log.info("Loading Main Screen");
		LoadingScreen ls = new LoadingScreen();
		final DieRoller dr = new DieRoller(ls);
		dr.pack();
		dr.setVisible(true);
		dr.setTitle("Storyteller System Dice Roller");
		dr.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ls.setVisible(false);
	}



	public static Point centreOnScreen(Dimension d)
	{
		Dimension c = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point((c.width - d.width) / 2, (c.height - d.height) / 2);
	}
}
