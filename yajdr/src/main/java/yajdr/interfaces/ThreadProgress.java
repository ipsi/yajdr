package yajdr.interfaces;

public interface ThreadProgress
{

	public void update(int current, int total);



	public void update(String message);



	public void resetProgressBar();
}
