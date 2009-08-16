package yajdr.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import yajdr.threads.D20XP;
import yajdr.util.StringUtil;

/**
 * This is the class that generates the GUI found in the XP Calculator pane of the primary frame.
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Andrew Thorburn
 * @version 6.0
 */

public class D20XpGui extends JPanel implements ActionListener
{

	private static final long	serialVersionUID	= -5695846833698389374L;

	private Vector<JTextField>	d20PartyNumVec		= new Vector<JTextField>();

	private Vector<JTextField>	d20CRVec			= new Vector<JTextField>();

	private Vector<JTextField>	d20NumMonstVec		= new Vector<JTextField>();

	private Vector<JTextField>	d20PartyMemLevVec	= new Vector<JTextField>();

	private Vector<D20XP>		threads;

	private Vector<Double>		xpHolder;

	private JButton				getXP				= new JButton("Calculate XP");

	private JTextArea			d20XPOutput			= new JTextArea(6, 15);

	private JCheckBox			sumCheckBox			= new JCheckBox("Sum", null, true);

	private int					finished			= 0;



	/**
	 * This constructs the panel that contains all buttons and fields relevant to calculating XP.
	 * Most Components are stored in Vectors, unlike the other classes, where they are traditionally
	 * stored as data fields.
	 */
	public D20XpGui()
	{
		for (int i = 0; i < 6; i++)
		{
			d20CRVec.addElement(new JTextField(5));
			d20NumMonstVec.addElement(new JTextField(5));
			d20PartyMemLevVec.addElement(new JTextField(5));
			d20PartyNumVec.addElement(new JTextField(5));
		}
		Box d20CR = new Box(1);
		d20CR.add(new JLabel("CR of Monster"));
		for (int i = 0; i < 6; i++)
		{
			d20CR.add(d20CRVec.elementAt(i));
		}

		Box NumMonstBox = new Box(1);
		NumMonstBox.add(new JLabel("# Monsters"));
		for (int i = 0; i < 6; i++)
		{
			NumMonstBox.add(d20NumMonstVec.elementAt(i));
		}

		Box PartyLevBox = new Box(1);
		PartyLevBox.add(new JLabel("Party Member Level"));
		for (int i = 0; i < 6; i++)
		{
			PartyLevBox.add(d20PartyMemLevVec.elementAt(i));
		}

		Box PartyNumBox = new Box(1);
		PartyNumBox.add(new JLabel("Number of People in Party"));
		for (int i = 0; i < 6; i++)
		{
			PartyNumBox.add(d20PartyNumVec.elementAt(i));
			d20PartyNumVec.elementAt(i).setText("4");
		}

		JPanel xpOutput = new JPanel();
		xpOutput.add(new JLabel("XP Recieved: "));
		xpOutput.add(d20XPOutput);
		xpOutput.add(getXP);

		JPanel temp = new JPanel();
		temp.add(d20CR);
		temp.add(NumMonstBox);
		temp.add(PartyLevBox);
		temp.add(PartyNumBox);

		JTextArea t = (JTextArea) xpOutput.getComponent(1);
		t.setBorder(BorderFactory.createLineBorder(Color.black, 1));

		Box xpBox = new Box(1);
		xpBox.add(temp);
		xpBox.add(xpOutput);
		xpBox.add(sumCheckBox);
		add(xpBox);

		getXP.addActionListener(this);
		sumCheckBox.addActionListener(this);
	}



	public void actionPerformed(ActionEvent ae)
	{
		Object buttonPressed = ae.getSource();

		if (buttonPressed == getXP)
		{
			if (sumCheckBox.isSelected())
			{
				sumCheck();
			}
			calcXP();
		}
		else if (buttonPressed == sumCheckBox && sumCheckBox.isSelected())
		{
			sumCheck();
		}
	}



	/**
	 * Not the same as the one in <code>d20XP</code>, though it is used to call it. It takes the
	 * values stored in each text field, turns them into an integer, and finally passes them to a
	 * new instance of <code>d20XP</code>, and then starts each thread.
	 */
	private void calcXP()
	{
		finished = 0;
		xpHolder = new Vector<Double>();
		threads = new Vector<D20XP>();
		d20XPOutput.setText("");
		for (int i = 0; i < 6; i++)
		{
			JTextField a = d20CRVec.elementAt(i);
			JTextField b = d20NumMonstVec.elementAt(i);
			JTextField c = d20PartyMemLevVec.elementAt(i);
			JTextField d = d20PartyNumVec.elementAt(i);
			boolean nonEmpty = checkEmpty(a, b, c, d);
			boolean allInts = allIntegers(a, b, c, d);
			if (nonEmpty && allInts)
			{
				threads.addElement(new D20XP(Integer.parseInt(c.getText()), Integer.parseInt(a.getText()), Integer.parseInt(b.getText()), Integer.parseInt(d.getText()), i, this));
			}
		}
		for (int i = 0; i < threads.size(); i++)
		{
			threads.elementAt(i).start();
		}
	}



	private boolean checkEmpty(JTextField a, JTextField b, JTextField c, JTextField d)
	{
		return !(a.getText()).equalsIgnoreCase("") && !(b.getText()).equalsIgnoreCase("") && !(c.getText()).equalsIgnoreCase("") && !(d.getText()).equalsIgnoreCase("");
	}



	private boolean allIntegers(JTextField a, JTextField b, JTextField c, JTextField d)
	{
		return StringUtil.isInteger(a.getText()) && StringUtil.isInteger(b.getText()) && StringUtil.isInteger(c.getText()) && StringUtil.isInteger(d.getText());
	}



	/**
	 * This is called when a thread finishes. Assuming the number of threads finished is equal to
	 * the total number of threads, then <code>showResult</code> is called.
	 * 
	 * @param xpr
	 *            double The amount of experience calculated by the thread.
	 * @param threadNum
	 *            int The number of thread, which is the number of places to the right of the first
	 *            series of text columns.
	 */
	public synchronized void finished(double xpr, int threadNum)
	{
		xpHolder.add(threadNum, new Double(xpr));
		finished++;
		if (finished == threads.size())
		{
			showResult();
		}
	}



	/**
	 * This is called after all threads have finished, and told this object that they are. It then
	 * looks to see if sumCheckBox is selected, and then either sums up all xp awards returned, or
	 * creates a new line for each award.
	 */
	private void showResult()
	{
		if (sumCheckBox.isSelected())
		{
			double sum = 0;
			for (int i = 0; i < xpHolder.size(); i++)
			{
				sum += xpHolder.elementAt(i).doubleValue();
			}
			d20XPOutput.setText("XP gained by individual: " + sum);
		}
		else
		{
			for (int i = 0; i < xpHolder.size(); i++)
			{
				d20XPOutput.append("XP Gained: " + xpHolder.elementAt(i).doubleValue() + "\n");
			}
		}
	}



	private void sumCheck()
	{
		String one = d20PartyMemLevVec.elementAt(0).getText();
		String two = d20PartyNumVec.elementAt(0).getText();
		for (int i = 0; i < 6; i++)
		{
			JTextField c = d20PartyMemLevVec.elementAt(i);
			JTextField d = d20PartyNumVec.elementAt(i);
			c.setText(one);
			d.setText(two);
		}
	}
}
