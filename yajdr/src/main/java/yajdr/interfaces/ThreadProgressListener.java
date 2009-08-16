package yajdr.interfaces;

public interface ThreadProgressListener<T>
{
	public void threadStarted(T threadInfo);



	public void threadProgress(T threadInfo);



	public void threadFinished(T threadInfo);
}
