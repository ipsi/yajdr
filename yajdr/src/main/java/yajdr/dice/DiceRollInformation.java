package yajdr.dice;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

public class DiceRollInformation implements Cloneable
{
	private int		difficulty		= -1;
	private int		diceToRoll		= -1;
	private int		rollAgainValue	= -1;
	private int		newThreadSize	= -1;
	private boolean	rerollTens		= false;
	private boolean	tensCountTwice	= false;

	private Logger	logger			= Logger.getLogger(DiceRollInformation.class);



	public DiceRollInformation()
	{
		// EMPTY CONSTRUCTOR - Use Setter Methods;
	}



	@Override
	public DiceRollInformation clone()
	{
		DiceRollInformation newInfo = new DiceRollInformation();
		try
		{
			BeanUtils.copyProperties(newInfo, this);
		}
		catch (IllegalAccessException e)
		{
			logger.error("IllegalAccessException trying to copy properties...", e);
		}
		catch (InvocationTargetException e)
		{
			logger.error("InvocationTargetException trying to copy properties...", e);
		}
		return newInfo;
	}



	/**
	 * @param difficulty
	 *            the difficulty to set
	 */
	public void setDifficulty(int difficulty)
	{
		this.difficulty = difficulty;
	}



	/**
	 * @return the difficulty
	 */
	public int getDifficulty()
	{
		return difficulty;
	}



	/**
	 * @param diceToRoll
	 *            the diceToRoll to set
	 */
	public void setDiceToRoll(int diceToRoll)
	{
		this.diceToRoll = diceToRoll;
	}



	/**
	 * @return the diceToRoll
	 */
	public int getDiceToRoll()
	{
		return diceToRoll;
	}



	/**
	 * @param rollAgainValue
	 *            the rollAgainValue to set
	 */
	public void setRollAgainValue(int rollAgainValue)
	{
		this.rollAgainValue = rollAgainValue;
	}



	/**
	 * @return the rollAgainValue
	 */
	public int getRollAgainValue()
	{
		return rollAgainValue;
	}



	/**
	 * @param rerollTens
	 *            the rerollTens to set
	 */
	public void setRerollTens(boolean rerollTens)
	{
		this.rerollTens = rerollTens;
	}



	/**
	 * @return the rerollTens
	 */
	public boolean getRerollTens()
	{
		return rerollTens;
	}



	/**
	 * @param tensCountTwice
	 *            the tensCountTwice to set
	 */
	public void setTensCountTwice(boolean tensCountTwice)
	{
		this.tensCountTwice = tensCountTwice;
	}



	/**
	 * @return the tensCountTwice
	 */
	public boolean getTensCountTwice()
	{
		return tensCountTwice;
	}



	/**
	 * @param newThreadSize the newThreadSize to set
	 */
	public void setNewThreadSize(int newThreadSize)
	{
		this.newThreadSize = newThreadSize;
	}



	/**
	 * @return the newThreadSize
	 */
	public int getNewThreadSize()
	{
		return newThreadSize;
	}

}
