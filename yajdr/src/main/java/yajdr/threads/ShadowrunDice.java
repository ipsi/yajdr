package yajdr.threads;

import java.io.Serializable;

import yajdr.gui.ShadowrunGui;


/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 */

public class ShadowrunDice extends Thread implements Serializable
{

	private static final long	serialVersionUID	= 2063703781640389401L;

	private int					dif, diceToRoll, successes = 0, botch = 0;

	private String				dieResult			= "";

	ShadowrunGui				sg;



	public ShadowrunDice(int diceToRoll, int dif, ShadowrunGui sg)
	{
		this.diceToRoll = diceToRoll;
		this.dif = dif;
		this.sg = sg;
	}



	public void run()
	{
		rollDice();
		sg.finished(successes, botch, dieResult);
	}



	private void rollDice()
	{
		for (int i = 0; i < diceToRoll; i++)
		{
			double dieRoll = Math.floor((Math.random() * 6) + 1);

			if (dieRoll >= dif)
			{
				successes++;
			}

			else if (dieRoll == 1)
			{
				botch++;
			}

			else if (dieRoll == 6 && dieRoll < dif)
			{
				double dieReRoll = 6;
				while (dieReRoll == 6)
				{
					dieReRoll = Math.floor((Math.random() * 6) + 1);
					if (dieRoll + dieReRoll >= dif)
					{
						successes++;
						dieRoll += dieReRoll;
						break;
					}
					dieRoll += dieReRoll;
				}
			}
			dieResult += (int) dieRoll + ", ";
		}
	}
}
