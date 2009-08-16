package yajdr.util;

public final class StringUtil
{

	private static final long		MILLISECONDS_SECOND		= 1000;
	private static final long		MILLISECONDS_MINUTE		= StringUtil.MILLISECONDS_SECOND * 60;
	private static final long		MILLISECONDS_HOUR		= StringUtil.MILLISECONDS_MINUTE * 60;
	private static final long		MILLISECONDS_DAY		= StringUtil.MILLISECONDS_HOUR * 24;
	private static final long		MILLISECONDS_YEAR		= StringUtil.MILLISECONDS_DAY * 365;

	private static final long[]		MILLISECOND_CONVERSIONS	= { 1, StringUtil.MILLISECONDS_SECOND, StringUtil.MILLISECONDS_MINUTE, StringUtil.MILLISECONDS_HOUR, StringUtil.MILLISECONDS_DAY, StringUtil.MILLISECONDS_YEAR };
	private static final String[]	TIME_TYPES				= { "ms", "s ", "m ", "h ", "d ", "y " };



	private StringUtil()
	{
		// NO INSTANTIATION
	}



	public static boolean isInteger(String s)
	{
		for (char c : s.toCharArray())
		{
			if (c < '0' || c > '9')
				return false;
		}

		return true;
	}



	public static String formatDuration(long duration)
	{
		String formattedDuration = "";

		for (int i = 0; i < StringUtil.MILLISECOND_CONVERSIONS.length; i++)
		{
			if (duration == 0)
				return formattedDuration.length() == 0 ? "0ms" : formattedDuration;

			long tmp = duration;
			if (i + 1 < StringUtil.MILLISECOND_CONVERSIONS.length)
				tmp = (tmp % StringUtil.MILLISECOND_CONVERSIONS[i + 1]);

			tmp = tmp / StringUtil.MILLISECOND_CONVERSIONS[i];
			duration -= tmp * StringUtil.MILLISECOND_CONVERSIONS[i];
			formattedDuration = String.format("%,d", tmp) + StringUtil.TIME_TYPES[i] + formattedDuration;
		}

		return formattedDuration;
	}
}
