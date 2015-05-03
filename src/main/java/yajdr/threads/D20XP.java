package yajdr.threads;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.JOptionPane;

import yajdr.gui.D20XpGui;

/**
 * This is a complex class, not in terms of what it does, but in terms of how it does it. This
 * program is designed to return the amount of experience someone recieves. For levels and CR < 20,
 * it reads them from an array, but for levels greater then 20, it uses a complex system to work out
 * the amount of XP that should be awarded for the given challenge rating.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 */

public class D20XP extends Thread implements Serializable
{

	private static final long	serialVersionUID	= -1295601107378180005L;

	/**
	 * This is the two-dimensional array that stores the XP awards a person should recieve. It will
	 * likely be serialised in the future, to make the program faster to load.
	 */
	private int[][]				xpTable;

	private int					level, CR, numMonst, numPeopleInParty;

	/**
	 * This is important, and you won't see it in any of the other threaded classes in this project,
	 * as it matters what order the threads finish in, if the checkbox is not checked in the GUI. If
	 * the threads finished in a different order than they started in, then it would take too much
	 * guesswork to figure out which line of XP belonged to which column
	 */
	private int					ThreadNumber;

	private D20XpGui			g;



	/**
	 * This sets the data fields in the class to the parameters passed, and initialises
	 * <code>xpTable</code>
	 * 
	 * @param level
	 *            int This is the level of the party member this thread is generating experience
	 *            for. It needs be non-negative. If it is < 0, then it will be set to 3 when
	 *            <code>calcXP</code> is executed.
	 * @param CR
	 *            int The challange rating of the monster. Again, must be > 0. If it is < 0, it will
	 *            be set to one when <code>calcXP</code> is executed
	 * @param numMonst
	 *            int The number of monsters at the given challenge rating the party faced. If < 0,
	 *            it will be set to 1.
	 * @param numPeopleInParty
	 *            int The number of people in the party.
	 * @param ThreadNumber
	 *            int The number of the thread, which is passed when the thread is created.
	 * @param g
	 *            d20xpGUI The GUI object that called the constructor.
	 */
	public D20XP(int level, int CR, int numMonst, int numPeopleInParty, int ThreadNumber, D20XpGui g)
	{
		this.level = level;
		this.CR = CR;
		this.numMonst = numMonst;
		this.numPeopleInParty = numPeopleInParty;
		this.ThreadNumber = ThreadNumber;
		this.g = g;

		xpTable = new int[20][20];
		try
		{
			FileInputStream inStream = new FileInputStream("xpTable.ser");
			ObjectInputStream ins = new ObjectInputStream(inStream);
			xpTable = (int[][]) ins.readObject();
			ins.close();

			for (int i = 0; i < xpTable.length; i++)
			{
				for (int j = 0; j < xpTable[i].length; j++)
				{
					System.out.print(xpTable[i][j] + (j + 1 == xpTable[i].length ? "" : "\t"));
				}
				System.out.println();
			}
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, "IOException");
		}
		catch (ClassNotFoundException e)
		{
			JOptionPane.showMessageDialog(null, "Class Not Found Exception");
		}
	}



	/**
	 * <code>run</code> is an implementation of a method required of all classes that extend
	 * <code>Thread
   * </code>, and simply calls <code>finished</code> in <code>d20xpGUI</code>.
	 * 
	 * @see D20XpGui
	 */
	public void run()
	{
		g.finished(calcXP(), ThreadNumber);
	}



	/**
	 * Calculates the xp that should be awarded based on the arguements passed to the constructer.
	 * If any of these are too low, they are set to 1. It then performs a series of operations, and
	 * returns the value. Potentially, it may call <code>epic</code> if level or cr are < 20.
	 * 
	 * @return double The experience points that should be recieved by the party member.
	 */
	public double calcXP()
	{

		if (level <= 0)
		{
			level = 1;
		}

		if (CR <= 0)
		{
			CR = 1;
		}

		if (numMonst <= 0)
		{
			numMonst = 1;
		}

		if (numPeopleInParty <= 0)
		{
			numPeopleInParty = 1;
		}

		if (level - CR >= 8 || level - CR <= -8)
		{
			return 0;
		}

		level -= 1;
		CR -= 1;

		try
		{
			return (((xpTable[level][CR]) * numMonst) / numPeopleInParty);
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return ((epic(level, CR) * numMonst) / numPeopleInParty);
		}
	}



	/**
	 * This is a complex method, primarily in it's execution. Essentially, the amount of xp when
	 * level > 20 can be figured out through equations, and this method runs through those
	 * equations.
	 * 
	 * @param level
	 *            int The level.
	 * @param CR
	 *            int Challange Rating.
	 * @return double The amount of xp that should be awarded for a monster of this CR, and a PC of
	 *         this level.
	 */
	private double epic(int level, int CR)
	{
		try
		{
			return xpTable[level][CR];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			if (CR >= 20 && level < 20)
			{
				return 2 * epic(level, CR - 2);
			}

			level += 1;
			CR += 1;
			double z = 300 * level;
			if (level == CR)
			{
				return z;
			}
			else if ((level - CR) % 2 == 0 && level - CR > 0)
			{
				for (int i = 0; i < ((level - CR) / 2); i++)
				{
					z /= 2;
				}
				return z;
			}
			else if ((level - CR) % 2 == 0 && level - CR < 0)
			{
				for (int i = 0; i > (level - CR) / 2; i--)
				{
					z *= 2;
				}
				return z;
			}
			else if ((level - CR) % 2 != 0 && level - CR > 0)
			{
				z /= (2.0 / 3.0);
				for (int i = 0; i < Math.floor(((double) level - (double) CR) / 2); i++)
				{
					z /= 2;
				}
				return z;
			}
			else if ((level - CR) % 2 != 0 && level - CR < 0)
			{
				z *= (2.0 / 3.0);
				for (int i = 0; i > Math.floor(((double) level - (double) CR) / 2); i--)
				{
					z *= 2;
				}
				return z;
			}
		}

		return -1;
	}
}
