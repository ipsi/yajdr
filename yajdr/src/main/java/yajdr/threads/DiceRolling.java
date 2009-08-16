package yajdr.threads;

import java.io.Serializable;

import yajdr.dice.DiceRollInformation;
import yajdr.interfaces.DiceRollerListener;

/**
 * This is the Dice Roller for all World of Darkness systems, and was the first one created for this
 * project.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 */

public class DiceRolling extends Thread implements Serializable
{

	private static final long	serialVersionUID	= 5221415169200696502L;

	private DiceRollerListener	listener			= null;

	private int					diceToRoll			= -1;
	private int					difficulty			= -1;
	private int					successCount		= -1;
	private int					botchCount			= -1;
	private int					rerollValue			= -1;

	private String				dieResult			= null;

	private boolean				tensCountTwice		= false, rerollTens = false;



	/**
	 * 
	 * 
	 * @param listener
	 * @param info
	 */
	public DiceRolling(DiceRollerListener listener, DiceRollInformation info)
	{
		this.listener = listener;
		this.diceToRoll = info.getDiceToRoll();
		this.difficulty = info.getDifficulty();
		this.rerollValue = info.getRollAgainValue();
		this.tensCountTwice = info.getTensCountTwice();
		this.rerollTens = info.getRerollTens();
	}



	/**
	 * The implementation of the run method, as required by <code>Thread</code>. Again, it starts
	 * the dice roller, increments various data fields by the values in this object, and then calls
	 * <code>isFinished</code>.
	 */
	@Override
	public void run()
	{
		listener.rollStarted();
		successCount = 0;
		botchCount = 0;
		dieResult = "";

		for (int i = 1; i <= diceToRoll; i++)
		{
			int dieRoll = (int) Math.floor(((Math.random()) * 10) + 1);

			if (tensCountTwice && dieRoll == 10)
			{
				successCount++;
			}

			if (rerollTens && dieRoll >= rerollValue)
			{
				diceToRoll++;
			}

			if (dieRoll >= difficulty)
			{
				successCount++;
			}
			else if (dieRoll == 1)
			{
				botchCount++;
			}

			dieResult += dieRoll + ", ";
		}
		listener.rollFinished(dieResult, successCount, botchCount);
	}
}
