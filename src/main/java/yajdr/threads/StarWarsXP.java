package yajdr.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import yajdr.gui.StarWarsXpGui;

public class StarWarsXP extends Thread
{
	private int[][]			xpTable	= new int[9][20];

	private StarWarsXpGui	g		= null;

	private int				level, code, peopleParty, encounters;



	public StarWarsXP(int level, int code, int peopleParty, int encounters, StarWarsXpGui g)
	{
		this.level = level;
		this.code = code;
		this.peopleParty = peopleParty;
		this.encounters = encounters;
		this.g = g;

		File xp = new File("c:\\swXP.txt");
		try
		{
			FileReader inStream = new FileReader(xp);
			BufferedReader ins = new BufferedReader(inStream);
			for (int i = 0; i < xpTable.length; i++)
			{
				for (int j = 0; j < xpTable[i].length; j++)
				{
					xpTable[i][j] = Integer.parseInt(ins.readLine());
				}
			}
		}
		catch (IOException ex)
		{
			System.out.println("IO Error " + ex.getMessage());
		}
		catch (NumberFormatException ex)
		{
			System.out.println("NFE " + ex.getMessage());
		}
	}



	private double calcXP()
	{
		if (level < 0 || peopleParty < 0)
		{
			return 0;
		}
		return ((xpTable[code][level - 1] * level) * encounters) / peopleParty;
	}



	public void run()
	{
		g.finished(calcXP());
	}
}
