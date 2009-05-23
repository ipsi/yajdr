package yajdr.interfaces;

public interface DiceRollerListener
{
	public void rollFinished(String resultString, int successes, int botches);
	
	public void rollStarted();
}
