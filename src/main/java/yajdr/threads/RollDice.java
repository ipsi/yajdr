package yajdr.threads;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import yajdr.dice.DiceRollInformation;
import yajdr.gui.WorldOfDarknessThreadInformation;
import yajdr.interfaces.DiceRollerListener;
import yajdr.interfaces.ThreadProgressListener;
import yajdr.util.StringUtil;

public class RollDice extends Thread implements DiceRollerListener
{
	private static final Log											log						= LogFactory.getLog(RollDice.class);

	private ThreadProgressListener<WorldOfDarknessThreadInformation>	listener;
	private DiceRollInformation											info					= null;
	private int															totalThreadCount		= -1;
	private int															completedThreadCount	= 0;
	private int															botchCount				= 0;
	private int															successCount			= 0;
	private String														result					= "";



	public RollDice(ThreadProgressListener<WorldOfDarknessThreadInformation> listener, DiceRollInformation info)
	{
		this.listener = listener;
		this.info = info;
	}



	@Override
	public void run()
	{
		log.debug("Setting up dice to roll");
		int diceToRoll = info.getDiceToRoll();

		DiceRolling[] dArray = null;

		if ((diceToRoll % info.getNewThreadSize()) == 0)
		{
			dArray = new DiceRolling[(diceToRoll / info.getNewThreadSize())];
		}
		else if (diceToRoll < info.getNewThreadSize())
		{
			dArray = new DiceRolling[1];
		}
		else
		{
			dArray = new DiceRolling[(int) (Math.floor(diceToRoll / info.getNewThreadSize())) + 1];
		}

		listener.threadStarted(null);
		totalThreadCount = dArray.length;

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < dArray.length; i++)
		{
			DiceRollInformation newInfo = info.clone();
			newInfo.setDiceToRoll((diceToRoll - info.getNewThreadSize() > 0) ? info.getNewThreadSize() : diceToRoll);

			if (diceToRoll - info.getNewThreadSize() > 0)
				diceToRoll -= info.getNewThreadSize();

			dArray[i] = new DiceRolling(this, newInfo);
			dArray[i].start();
		}

		do
		{
			try
			{
				synchronized (this)
				{
					this.wait(100);
				}
			}
			catch (InterruptedException e)
			{
				log.debug("Interrupted thread waiting for results...", e);
			}

			WorldOfDarknessThreadInformation threadInfo = new WorldOfDarknessThreadInformation();

			threadInfo.setBotchCount(botchCount);
			threadInfo.setSuccessCount(successCount);
			threadInfo.setTotalThreadCount(totalThreadCount);
			threadInfo.setCompletedThreadCount(completedThreadCount);
			threadInfo.setDiceRollInformation(info);

			listener.threadProgress(threadInfo);
		}
		while (completedThreadCount < totalThreadCount);

		long endTime = System.currentTimeMillis();

		log.trace("Rollling [" + info.getDiceToRoll() + "] dice, with [" + info.getNewThreadSize() + "] dice per thread took [" + StringUtil.formatDuration(endTime - startTime) + "].");

		WorldOfDarknessThreadInformation threadInfo = new WorldOfDarknessThreadInformation();

		threadInfo.setBotchCount(botchCount);
		threadInfo.setSuccessCount(successCount);
		threadInfo.setTotalThreadCount(totalThreadCount);
		threadInfo.setCompletedThreadCount(completedThreadCount);
		threadInfo.setResult(result);
		threadInfo.setDiceRollInformation(info);

		listener.threadFinished(threadInfo);
	}



	public synchronized void rollFinished(String resultString, int successes, int botches)
	{
		this.successCount += successes;
		this.botchCount += botches;
		this.result += resultString;
		completedThreadCount++;
		this.notify();
	}



	public void rollStarted()
	{
		// if(log.isDebugEnabled())
		// log.debug("Thread [" + Thread.currentThread().getName() + "] started executing...");
	}
}
