package yajdr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ResourceUtil
{
	private static final Logger logger	= LoggerFactory.getLogger(ResourceUtil.class);



	public static String loadResourceAsString(String resourceName)
	{
		return ResourceUtil.loadResourceAsString(resourceName, Thread.currentThread().getContextClassLoader());
	}



	public static String loadResourceAsString(String resourceName, ClassLoader cl)
	{
		InputStream is = cl.getResourceAsStream(resourceName);

		if (is == null)
		{
			logger.error("Can't find file [" + resourceName + "]!");
			return null;
		}

		BufferedReader bir = new BufferedReader(new InputStreamReader(is));

		try
		{
			char[] in = new char[8192];
			int count = bir.read(in);
			StringBuffer buffer = new StringBuffer();
			while (count > 0)
			{
				char[] tmp = null;
				if (count < in.length)
				{
					tmp = new char[count];
					System.arraycopy(in, 0, tmp, 0, count);
				}
				buffer.append(tmp == null ? in : tmp);
				tmp = null;
				count = bir.read(in);
			}

			return buffer.toString();
		}
		catch (IOException e)
		{
			logger.error("There was an error loading the file [" + resourceName + "]", e);
		}

		return null;

	}
}
