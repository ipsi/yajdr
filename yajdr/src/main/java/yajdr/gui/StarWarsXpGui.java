package yajdr.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import yajdr.interfaces.ThreadProgress;
import yajdr.threads.StarWarsXP;
import yajdr.util.StringUtil;


public class StarWarsXpGui extends JPanel implements ActionListener
{

	private static final long	serialVersionUID	= -1981480468315205572L;

	private JButton				getXP				= new JButton("Get Experience");

	private JTextField			level				= new JTextField(2);

	private JTextField[]		challengeCode		= new JTextField[3];

	private JTextField			peopleParty			= new JTextField(2);

	private JTextField[]		numberEncounters	= new JTextField[3];

	private JTextField			output				= new JTextField(10);

	private int					totalXP, finished;

	private StarWarsXP[]		sw					= null;

	private ThreadProgress		parent;



	public StarWarsXpGui()
	{
		for (int i = 0; i < challengeCode.length; i++)
		{
			challengeCode[i] = new JTextField(2);
		}

		for (int i = 0; i < numberEncounters.length; i++)
		{
			numberEncounters[i] = new JTextField(2);
		}

		Box levelBox = new Box(1);
		levelBox.add(new JLabel("Level"));
		levelBox.add(level);

		Box codeBox = new Box(1);
		codeBox.add(new JLabel("Challenge Code"));
		codeBox.add(challengeCode[0]);
		codeBox.add(challengeCode[1]);
		codeBox.add(challengeCode[2]);

		Box peopleBox = new Box(1);
		peopleBox.add(new JLabel("Number of people in Party"));
		peopleBox.add(peopleParty);

		Box encounterBox = new Box(1);
		encounterBox.add(new JLabel("Number of encounters"));
		encounterBox.add(numberEncounters[0]);
		encounterBox.add(numberEncounters[1]);
		encounterBox.add(numberEncounters[2]);

		JPanel temp = new JPanel();
		temp.add(levelBox);
		temp.add(codeBox);
		temp.add(encounterBox);
		temp.add(peopleBox);

		Box all = new Box(1);
		all.add(temp);
		all.add(output);
		all.add(getXP);
		getXP.addActionListener(this);

		add(all);
	}



	public void actionPerformed(ActionEvent ae)
	{
		Object buttonPressed = ae.getSource();
		if (buttonPressed == getXP)
		{
			getXP();
		}
	}



	private void getXP()
	{
		finished = 0;
		totalXP = 0;
		sw = new StarWarsXP[arraySize()];
		parent.update("Getting XP...");

		int z = 0;

		for (int i = 0; i < 3; i++)
		{
			if (StringUtil.isInteger(level.getText()) && StringUtil.isInteger(peopleParty.getText()) && StringUtil.isInteger(numberEncounters[i].getText()) && getCode(i) != -1)
			{
				int lvl = Integer.parseInt(level.getText());
				int ppl = Integer.parseInt(peopleParty.getText());
				int num = Integer.parseInt(numberEncounters[i].getText());

				int code = getCode(i);
				sw[z] = new StarWarsXP(lvl, code, ppl, num, this);
				sw[z].start();
				z++;
			}
		}
	}



	public synchronized void finished(double XP)
	{
		finished++;
		totalXP += XP;
		parent.update(finished, sw.length);
		if (finished == sw.length)
		{
			output.setText("XP for each person is: " + totalXP);
			parent.update("Done");
			parent.resetProgressBar();
		}
	}



	public void setParent(ThreadProgress tp)
	{
		parent = tp;
	}



	private int arraySize()
	{
		int length = 0;
		for (int i = 0; i < 3; i++)
		{
			if (StringUtil.isInteger(level.getText()) && StringUtil.isInteger(peopleParty.getText()) && StringUtil.isInteger(numberEncounters[i].getText()) && getCode(i) != -1)
			{
				length++;
			}
		}
		return length;
	}



	private int getCode(int i)
	{
		int code = -1;
		if (challengeCode[i].getText().equalsIgnoreCase("a"))
		{
			code = 0;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("b"))
		{
			code = 1;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("c"))
		{
			code = 2;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("d"))
		{
			code = 3;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("e"))
		{
			code = 4;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("f"))
		{
			code = 5;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("g"))
		{
			code = 6;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("h"))
		{
			code = 7;
		}
		else if (challengeCode[i].getText().equalsIgnoreCase("i"))
		{
			code = 8;
		}
		return code;
	}
}
