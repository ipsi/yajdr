package yajdr.gui;

import yajdr.dice.DiceRollInformation;

public class WorldOfDarknessThreadInformation
{
	private int					successCount			= -1;
	private int					botchCount				= -1;
	private String				result					= null;

	private int					totalThreadCount		= -1;
	private int					completedThreadCount	= -1;

	private DiceRollInformation	diceRollInformation		= null;



	/**
	 * @param successCount
	 *            the successCount to set
	 */
	public void setSuccessCount(int successCount)
	{
		this.successCount = successCount;
	}



	/**
	 * @return the successCount
	 */
	public int getSuccessCount()
	{
		return successCount;
	}



	/**
	 * @param botchCount
	 *            the botchCount to set
	 */
	public void setBotchCount(int botchCount)
	{
		this.botchCount = botchCount;
	}



	/**
	 * @return the botchCount
	 */
	public int getBotchCount()
	{
		return botchCount;
	}



	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result)
	{
		this.result = result;
	}



	/**
	 * @return the result
	 */
	public String getResult()
	{
		return result;
	}



	/**
	 * @param totalThreadCount
	 *            the totalThreadCount to set
	 */
	public void setTotalThreadCount(int totalThreadCount)
	{
		this.totalThreadCount = totalThreadCount;
	}



	/**
	 * @return the totalThreadCount
	 */
	public int getTotalThreadCount()
	{
		return totalThreadCount;
	}



	/**
	 * @param completedThreadCount
	 *            the completedThreadCount to set
	 */
	public void setCompletedThreadCount(int completedThreadCount)
	{
		this.completedThreadCount = completedThreadCount;
	}



	/**
	 * @return the completedThreadCount
	 */
	public int getCompletedThreadCount()
	{
		return completedThreadCount;
	}



	/**
	 * @param diceRollInformation
	 *            the diceRollInformation to set
	 */
	public void setDiceRollInformation(DiceRollInformation diceRollInformation)
	{
		this.diceRollInformation = diceRollInformation;
	}



	/**
	 * @return the diceRollInformation
	 */
	public DiceRollInformation getDiceRollInformation()
	{
		return diceRollInformation;
	}
}
