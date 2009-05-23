package yajdr.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import yajdr.interfaces.ThreadProgress;
import yajdr.threads.D20DiceRolling;


/**
 * This is the GUI for the d20 Dice Roller.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 * @todo Add methods to change and return the <code>newThread</code> data field.
 */

public class D20Gui extends JPanel implements ActionListener
{

	private static final long	serialVersionUID	= -4101366042949105622L;

	private SpinnerNumberModel	numDiceModel		= new SpinnerNumberModel(1, 1, 99, 1);

	private SpinnerNumberModel	diceSizeModel		= new SpinnerNumberModel(20, 1, 100, 1);

	private JSpinner			numDice				= new JSpinner(numDiceModel);

	private JSpinner			diceSize			= new JSpinner(diceSizeModel);

	private JTextField			d20NumAdd			= new JTextField("0", 3);

	private JTextArea			d20Output			= new JTextArea(6, 45);

	private JTextArea			d20Total			= new JTextArea(1, 45);

	private JLabel				d20DiceSizeLab		= new JLabel("Dice Size: ");

	private JLabel				d20NumDiceLab		= new JLabel("Number of dice to Roll: ");

	private JLabel				d20NumAddLab		= new JLabel("Number to Add: ");

	private JButton				d20RollDice			= new JButton("Roll Dice");

	private JButton[]			diceSizeArray		= { new JButton("Roll d2"), new JButton("Roll d3"), new JButton(new ImageIcon("Images" + File.separator + "dice_4_1.png")), new JButton(new ImageIcon("Images" + File.separator + "dice_6_1.png")), new JButton(new ImageIcon("Images" + File.separator + "dice_8_1.png")), new JButton(new ImageIcon("Images" + File.separator + "dice_10_1.png")), new JButton(new ImageIcon("Images" + File.separator + "dice_12_1.png")), new JButton(new ImageIcon("Images" + File.separator + "dice_20_1.png")), new JButton("Roll d%") };

	private JButton[]			d20PresetDiceArray	= { new JButton("Roll 1 die"), new JButton("Roll 2 dice"), new JButton("Roll 3 dice"), new JButton("Roll 4 dice"), new JButton("Roll 5 dice"), new JButton("Roll 6 dice"), new JButton("Roll 7 dice"), new JButton("Roll 8 dice"), new JButton("Roll 9 dice"), new JButton("Roll 10 dice") };

	private JScrollPane			d20ScrollPane		= new JScrollPane(d20Output);

	private int					d20DiceSum;

	private int					d20finished, numAdd;

	private String				dieResultStr		= "";

	private D20DiceRolling[]	d20Array;

	/**
	 * This is the field that stores the number of dice that should be assigned to each thread.
	 * There are no ways to modify this number at current, but they may be implemented later.
	 */
	private int					newThread			= 2000;



	/**
	 * This creates a new instance of d20GUI, and this takes care of adding the majority of
	 * datafields to this object. It also registers <code>actionListeners</code> for the buttons on
	 * the panel.
	 */
	public D20Gui()
	{
		JPanel d20Base = new JPanel();
		d20Base.add(d20NumDiceLab);
		d20Base.add(numDice);
		d20Base.add(d20RollDice);
		d20Base.add(d20DiceSizeLab);
		d20Base.add(diceSize);
		d20Base.add(d20NumAddLab);
		d20Base.add(d20NumAdd);

		JPanel d20PresetDice1 = new JPanel();
		for (int i = 0; i < (Math.floor(d20PresetDiceArray.length) / 2); i++)
		{
			d20PresetDice1.add(d20PresetDiceArray[i]);
			d20PresetDiceArray[i].addActionListener(this);
		}

		JPanel d20PresetDice2 = new JPanel();
		for (int i = (int) (Math.floor(d20PresetDiceArray.length) / 2); i < d20PresetDiceArray.length; i++)
		{
			d20PresetDice2.add(d20PresetDiceArray[i]);
			d20PresetDiceArray[i].addActionListener(this);
		}

		JPanel d20PresetDiceSizePanel = new JPanel();
		for (int i = 0; i < (Math.floor(diceSizeArray.length) / 2); i++)
		{
			d20PresetDiceSizePanel.add(diceSizeArray[i]);
			diceSizeArray[i].addActionListener(this);
		}

		JPanel d20PresetDiceSizePanel2 = new JPanel();
		for (int i = (int) (Math.floor(diceSizeArray.length) / 2); i < diceSizeArray.length; i++)
		{
			d20PresetDiceSizePanel2.add(diceSizeArray[i]);
			diceSizeArray[i].addActionListener(this);
		}

		JPanel d20OutputPanel = new JPanel();
		d20OutputPanel.add(d20ScrollPane);

		JPanel d20TotalPanel = new JPanel();
		d20TotalPanel.add(d20Total);

		Box d20Cont = new Box(1);
		d20Cont.add(d20Base);
		d20Cont.add(d20OutputPanel);
		d20Cont.add(d20TotalPanel);
		d20Cont.add(d20PresetDice1);
		d20Cont.add(d20PresetDice2);
		d20Cont.add(d20PresetDiceSizePanel);
		d20Cont.add(d20PresetDiceSizePanel2);
		add(d20Cont);

		d20RollDice.addActionListener(this);

		d20Output.setBorder(BorderFactory.createLineBorder(Color.black));
		d20Total.setBorder(BorderFactory.createLineBorder(Color.black));

		d20Output.setLineWrap(true);
	}



	/**
	 * This is called when a button is pressed that belongs to this panel. It only calls other
	 * methods, and doesn't actually do any of the work itself.
	 * 
	 * @param ae
	 *            ActionEvent
	 */
	public synchronized void actionPerformed(ActionEvent ae)
	{
		Object buttonPressed = ae.getSource();

		if (buttonPressed == d20RollDice)
		{
			d20RollDice();
		}
		else
		{
			for (int i = 0; i < diceSizeArray.length; i++)
			{
				if (buttonPressed == diceSizeArray[i])
				{
					d20buttonPressed(i);
				}
			}

			for (int i = 0; i < d20PresetDiceArray.length; i++)
			{
				if (buttonPressed == d20PresetDiceArray[i])
				{
					numDice.setValue(new Integer(i + 1));
					d20RollDice();
				}
			}
		}
	}



	/**
	 * This takes care of getting the number of dice, the dice size, and translating it all into a
	 * number of threads.
	 * <p>
	 * The threads are all stored in an array of <code>d20DiceRolling</code> objects, the size of
	 * which is determined by the number of dice being rolled. This does not, however, take note of
	 * when a thread is finished. That responsibilty is handled by <code>d20IsFinished</code>
	 * </p>
	 * 
	 * @see <code>d20IsFinished</code>
	 */
	private void d20RollDice()
	{
		String numAddStr = d20NumAdd.getText();
		d20DiceSum = 0;
		dieResultStr = "";

		int dS = ((Integer) diceSize.getValue()).intValue();
		int numD = ((Integer) numDice.getValue()).intValue();
		numAdd = Integer.parseInt(numAddStr);

		d20finished = 0;

		if ((numD % newThread) == 0)
		{
			d20Array = new D20DiceRolling[(numD / newThread)];
		}
		else if (numD < newThread)
		{
			d20Array = new D20DiceRolling[1];
		}
		else
		{
			d20Array = new D20DiceRolling[(numD / newThread) + 1];
		}

		for (int i = 0; i < d20Array.length; i++)
		{
			if ((numD - newThread) > 0)
			{
				numD -= newThread;
				d20Array[i] = new D20DiceRolling(this, numD, dS);
				d20Array[i].start();
			}
			else if (numD > 0)
			{
				d20Array[i] = new D20DiceRolling(this, numD, dS);
				d20Array[i].start();
			}
		}
	}



	/**
	 * This handles setting the on screen output. Very simple.
	 */
	private void d20SetOutput()
	{
		d20Total.setText("" + (d20DiceSum + numAdd));
		d20Output.setText(dieResultStr + " + " + numAdd);
	}



	/**
	 * <code>d20buttonPressed</code> really only exists to make it easier to set the dice size,
	 * which is not related to the position in the array, which stores these elements.
	 * 
	 * @param i
	 *            int The position of the button in the array which caused this method to be called.
	 */
	private void d20buttonPressed(int i)
	{
		if (i == 0)
		{
			diceSize.setValue(new Integer(2));
			d20RollDice();
		}
		else if (i == 1)
		{
			diceSize.setValue(new Integer(3));
			d20RollDice();
		}
		else if (i == 2)
		{
			diceSize.setValue(new Integer(4));
			d20RollDice();
		}
		else if (i == 3)
		{
			diceSize.setValue(new Integer(6));
			d20RollDice();
		}
		else if (i == 4)
		{
			diceSize.setValue(new Integer(8));
			d20RollDice();
		}
		else if (i == 5)
		{
			diceSize.setValue(new Integer(10));
			d20RollDice();
		}
		else if (i == 6)
		{
			diceSize.setValue(new Integer(12));
			d20RollDice();
		}
		else if (i == 7)
		{
			diceSize.setValue(new Integer(20));
			d20RollDice();
		}
		else if (i == 8)
		{
			diceSize.setValue(new Integer(100));
			d20RollDice();
		}
	}



	/**
	 * Called when a thread finishes, the primary function of this method is to provide a way for
	 * the GUI to know when a thread has run through to completion, and to know when all such
	 * threads have completed, so it can call <code>d20SetOutput</code>, and update the text area.
	 * 
	 * @see d20SetOutput
	 * @param result
	 *            String
	 * @param sum
	 *            int
	 */
	public synchronized void d20IsFinished(String result, int sum)
	{
		d20DiceSum += sum;
		dieResultStr += result;
		d20finished++;
		if (d20finished == d20Array.length)
		{
			d20SetOutput();
		}
	}



	public void setParent(ThreadProgress tp)
	{
		// NOT IMPLEMENTED
	}
}
