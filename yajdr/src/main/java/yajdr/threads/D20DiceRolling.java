package yajdr.threads;

import java.io.Serializable;

import yajdr.gui.D20Gui;


/**
 * This rolls the dice for the d20 part of the program.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 */

public class D20DiceRolling extends Thread implements Serializable
{
	private static final long	serialVersionUID	= -4285680232830844450L;

	private int					numD, dS;

	private int					d20DiceSum;

	private String				dieResultStr;

	private D20Gui				d;



	/**
	 * <p>
	 * Creates a new instance of the class, and sets the variables needed to roll the dice, as they
	 * cannot be passed when the thread is started
	 * </p>
	 * 
	 * @param d
	 *            d20GUI An instance of d20GUI, which is always the one the called the constructor.
	 * @param numD
	 *            int The number of dice to roll.
	 * @param dS
	 *            int The dice size.
	 */
	public D20DiceRolling(D20Gui d, int numD, int dS)
	{
		this.d = d;
		this.numD = numD;
		this.dS = dS;
	}



	/**
	 * <p>
	 * This is the run method, which is a required implementation when extending the thread class.
	 * This particular implementiation calls <code>d20RollDice</code>, increments the data fields
	 * that hold the results of all instances of <code>d20DiceRolling</code>, and then calls
	 * <code>d20IsFinished</code>, to tell the GUI the this thread is finished.
	 * </p>
	 */
	public void run()
	{
		d20RollDice();
		d.d20IsFinished(dieResultStr, d20DiceSum);
	}



	/**
	 * <p>
	 * A very simple method, <code>d20RollDice</code> generates a random number using
	 * <code>Math.random</code>, and multiplies it by the dice size, and finally adds one. It does
	 * this a number of times equal to <code>numD</code>.
	 * </p>
	 */
	private void d20RollDice()
	{
		d20DiceSum = 0;
		dieResultStr = "";

		for (int i = 1; i <= numD; i++)
		{
			int dieRoll = (int) Math.floor(((Math.random()) * dS) + 1);

			d20DiceSum += dieRoll;

			dieResultStr += dieRoll + ", ";
		}
	}
}
